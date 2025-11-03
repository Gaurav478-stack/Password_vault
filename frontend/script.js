// frontend/script.js
// Minimal client-side code to talk to backend auth & password API.
// Assumes index.html contains the elements with the same IDs as earlier.

// Auto-detect: if on Render domain, use relative paths; otherwise use full Render URL
const API_BASE = window.location.hostname.includes('onrender.com') 
  ? '' // Relative path when on Render
  : 'https://password-vault-2ow2.onrender.com'; // Full URL when on GitHub Pages

// DOM selectors (must match ids in index.html)
const authModal = document.getElementById('authModal');
const authTitle = document.getElementById('authTitle');
const authSubmit = document.getElementById('authSubmit');
const authCancel = document.getElementById('authCancel');
const authUsername = document.getElementById('authUsername');
const authPassword = document.getElementById('authPassword');

const vaultArea = document.getElementById('vaultArea');
const lockBtn = document.getElementById('lockBtn');
const exportBtn = document.getElementById('exportBtn');
const importBtn = document.getElementById('importBtn');

const addBtn = document.getElementById('addBtn');
const addPasswordBtn = document.getElementById('addPasswordBtn');
const entryModal = document.getElementById('entryModal');
const entryTitle = document.getElementById('entryTitle');
const entrySave = document.getElementById('entrySave');
const entryCancel = document.getElementById('entryCancel');
const serviceInput = document.getElementById('service');
const urlInput = document.getElementById('url');
const userInput = document.getElementById('user');
const pwInput = document.getElementById('pw');
const genBtn = document.getElementById('genBtn');
const togglePw = document.getElementById('togglePw');
const categorySelect = document.getElementById('categorySelect');
const passwordGrid = document.getElementById('passwordGrid');
const emptyState = document.getElementById('emptyState');
const searchInput = document.getElementById('searchInput');
const quickFilter = document.getElementById('quickFilter');
const clearAllBtn = document.getElementById('clearAllBtn');
const strengthFill = document.getElementById('strengthFill');

let vault = { entries: [] };
let currentlyEditingId = null;

function showAuthModal(isRegister=false){
  authModal.classList.remove('hidden');
  authModal.setAttribute('aria-hidden','false');
  authTitle.textContent = isRegister ? 'Create Account' : 'Unlock Your Vault';
  authPassword.value = '';
}

function hideAuthModal(){
  authModal.classList.add('hidden');
  authModal.setAttribute('aria-hidden','true');
}

async function apiFetch(path, method='GET', body=null){
  const headers = { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('token');
  if (token) headers['Authorization'] = 'Bearer ' + token;
  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });
  return res;
}

async function handleAuthSubmit(){
  const username = authUsername.value.trim();
  const password = authPassword.value;
  if (!username || !password) return alert('Enter username and password');

  // Try login
  let res = await apiFetch('/api/auth/login', 'POST', { username, password });
  const data = await res.json();
  if (res.ok) {
    localStorage.setItem('token', data.token);
    hideAuthModal();
    await loadVault();
    return;
  }

  // if login failed, ask to register
  if (confirm('Login failed. Create a new account with these credentials?')) {
    const r2 = await apiFetch('/api/auth/register', 'POST', { username, password });
    const d2 = await r2.json();
    if (!r2.ok) return alert('Register failed: ' + (d2.message || r2.status));
    alert('Account created. Logging in...');
    // login now
    const r3 = await apiFetch('/api/auth/login', 'POST', { username, password });
    const d3 = await r3.json();
    if (r3.ok) {
      localStorage.setItem('token', d3.token);
      hideAuthModal();
      await loadVault();
    } else {
      alert('Auto login failed. Please log in.');
    }
  } else {
    // user chose not to create account
  }
}

authSubmit.addEventListener('click', handleAuthSubmit);
authCancel.addEventListener('click', ()=>{ hideAuthModal(); });

lockBtn.addEventListener('click', ()=>{
  if (localStorage.getItem('token')) {
    if (!confirm('Logout?')) return;
    localStorage.removeItem('token');
    vault = { entries: [] };
    vaultArea.style.display = 'none';
    showAuthModal(false);
    lockBtn.textContent = 'Unlock';
  } else {
    showAuthModal(false);
  }
});

async function loadVault(){
  const token = localStorage.getItem('token');
  if (!token) return showAuthModal(false);
  const res = await apiFetch('/api/passwords', 'GET');
  if (res.status === 401) {
    localStorage.removeItem('token');
    showAuthModal(false);
    return;
  }
  const list = await res.json();
  vault.entries = list.map(e => ({ id: e._id, ...e }));
  vaultArea.style.display = '';
  renderEntries();
  lockBtn.textContent = 'Lock';
}

function renderEntries(filterText='', categoryFilter=''){
  const list = vault.entries || [];
  const q = (filterText || searchInput.value || quickFilter.value || '').trim().toLowerCase();
  let shown = list.filter(e => {
    if (q && !( (e.service||'').toLowerCase().includes(q) || (e.username||'').toLowerCase().includes(q) )) return false;
    if (categoryFilter && categoryFilter !== '' && e.category !== categoryFilter) return false;
    return true;
  });

  passwordGrid.innerHTML = '';
  if (!shown.length) {
    emptyState.classList.remove('hidden');
    return;
  }
  emptyState.classList.add('hidden');

  shown.forEach(entry => {
    const el = document.createElement('div');
    el.className = 'card';
    el.innerHTML = `
      <div class="row" style="gap:8px">
        <div>
          <strong>${escapeHtml(entry.service)}</strong>
          <div style="color:var(--muted);font-size:13px">${escapeHtml(entry.username || '')}</div>
        </div>
        <div style="display:flex;gap:6px;align-items:center">
          <button class="action-btn small reveal" data-id="${entry.id}" title="Reveal"><i class="far fa-eye"></i></button>
          <button class="action-btn small copy" data-id="${entry.id}" title="Copy password"><i class="far fa-copy"></i></button>
          <button class="action-btn small edit" data-id="${entry.id}" title="Edit"><i class="fas fa-edit"></i></button>
          <button class="action-btn small del" data-id="${entry.id}" title="Delete"><i class="fas fa-trash"></i></button>
        </div>
      </div>
      <div style="margin-top:12px">
        <div class="password-value" id="pw_${entry.id}">••••••••</div>
        <div style="margin-top:8px;color:var(--muted);font-size:12px">${entry.url ? `<a href="${escapeAttr(entry.url)}" target="_blank" style="color:var(--muted)">Visit</a>` : ''} <span style="float:right">${new Date(entry.updatedAt||entry.createdAt).toLocaleString()}</span></div>
      </div>
    `;
    passwordGrid.appendChild(el);
  });

  // attach handlers
  passwordGrid.querySelectorAll('.reveal').forEach(btn=>{
    btn.addEventListener('click', ev=>{
      const id = btn.dataset.id;
      const entry = vault.entries.find(e => e.id === id);
      if (!entry) return;
      const container = document.getElementById('pw_' + id);
      container.textContent = entry.password;
      setTimeout(()=>{ container.textContent = '••••••••'; }, 6000);
    });
  });

  passwordGrid.querySelectorAll('.copy').forEach(btn=>{
    btn.addEventListener('click', async ev=>{
      const id = btn.dataset.id;
      const entry = vault.entries.find(e => e.id === id);
      if (!entry) return;
      try {
        await navigator.clipboard.writeText(entry.password);
        btn.innerHTML = '<i class="fas fa-check"></i>';
        setTimeout(()=> btn.innerHTML = '<i class="far fa-copy"></i>', 1500);
      } catch (err) {
        alert('Copy failed: ' + err.message);
      }
    });
  });

  passwordGrid.querySelectorAll('.edit').forEach(btn=>{
    btn.addEventListener('click', ev=>{
      openEntryEditor(btn.dataset.id);
    });
  });

  passwordGrid.querySelectorAll('.del').forEach(btn=>{
    btn.addEventListener('click', async ev=>{
      const id = btn.dataset.id;
      if (!confirm('Delete this entry?')) return;
      const res = await apiFetch('/api/passwords/' + id, 'DELETE');
      if (!res.ok) return alert('Delete failed');
      await loadVault();
    });
  });
}

function escapeHtml(s){ if(!s) return ''; return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }
function escapeAttr(s){ if (!s) return ''; return String(s).replace(/"/g,'&quot;'); }

addBtn.addEventListener('click', ()=> openEntryEditor(null));
addPasswordBtn.addEventListener('click', ()=> openEntryEditor(null));
entryCancel.addEventListener('click', closeEntryEditor);

function openEntryEditor(id=null){
  currentlyEditingId = id;
  entryModal.classList.remove('hidden');
  entryModal.setAttribute('aria-hidden','false');
  if (!id){
    entryTitle.textContent = 'Add Password';
    serviceInput.value = '';
    urlInput.value = '';
    userInput.value = '';
    pwInput.value = '';
    categorySelect.value = '';
  } else {
    entryTitle.textContent = 'Edit Password';
    const e = vault.entries.find(x => x.id === id);
    if (!e) return alert('Entry not found');
    serviceInput.value = e.service;
    urlInput.value = e.url;
    userInput.value = e.username;
    pwInput.value = e.password;
    categorySelect.value = e.category || '';
  }
  updateStrength();
}

function closeEntryEditor(){
  currentlyEditingId = null;
  entryModal.classList.add('hidden');
  entryModal.setAttribute('aria-hidden','true');
}

genBtn.addEventListener('click', ()=> {
  pwInput.value = generateStrongPassword(16);
  updateStrength();
});

togglePw.addEventListener('click', ()=>{
  if (pwInput.type === 'text') { pwInput.type = 'password'; togglePw.innerHTML = '<i class="far fa-eye"></i>'; }
  else { pwInput.type = 'text'; togglePw.innerHTML = '<i class="far fa-eye-slash"></i>'; }
});

entrySave.addEventListener('click', async ()=> {
  const body = {
    service: serviceInput.value.trim(),
    url: urlInput.value.trim(),
    username: userInput.value.trim(),
    password: pwInput.value,
    category: categorySelect.value,
    icon: ''
  };
  if (!body.service || !body.username || !body.password) {
    if (!confirm('Some fields are empty. Save anyway?')) return;
  }
  try {
    if (!currentlyEditingId) {
      const res = await apiFetch('/api/passwords', 'POST', body);
      if (!res.ok) { const d = await res.json(); return alert('Save failed: ' + (d.message || res.status)); }
    } else {
      const res = await apiFetch('/api/passwords/' + currentlyEditingId, 'PUT', body);
      if (!res.ok) { const d = await res.json(); return alert('Update failed: ' + (d.message || res.status)); }
    }
    closeEntryEditor();
    await loadVault();
  } catch (err) {
    alert('Error: ' + err.message);
  }
});

function generateStrongPassword(len=12){
  const set = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+~`|}{[]:;?><,./-=";
  let s = '';
  const rnd = crypto.getRandomValues(new Uint32Array(len));
  for (let i=0; i<len; i++) s += set[rnd[i] % set.length];
  return s;
}

function updateStrength(){
  const p = pwInput.value || '';
  let score = 0;
  if (p.length >= 8) score += 30;
  if (/[A-Z]/.test(p)) score += 20;
  if (/[0-9]/.test(p)) score += 25;
  if (/[^A-Za-z0-9]/.test(p)) score += 25;
  score = Math.min(100, score);
  strengthFill.style.width = score + '%';
  strengthFill.className = 'strength-fill ' + (score < 50 ? 'strength-weak' : (score < 75 ? 'strength-medium' : 'strength-strong'));
}

searchInput.addEventListener('input', ()=> renderEntries(searchInput.value, getActiveCategory()));
quickFilter.addEventListener('input', ()=> renderEntries(quickFilter.value, getActiveCategory()));

document.addEventListener('DOMContentLoaded', async ()=>{
  const token = localStorage.getItem('token');
  if (!token) {
    showAuthModal(false);
    return;
  }
  await loadVault();
});

function getActiveCategory(){
  const el = document.querySelector('.category-item.active');
  return el ? el.dataset.cat : '';
}
document.querySelectorAll('.category-item').forEach(item=>{
  item.addEventListener('click', ()=>{
    document.querySelectorAll('.category-item').forEach(i=>i.classList.remove('active'));
    item.classList.add('active');
    renderEntries(searchInput.value, item.dataset.cat);
  });
});
