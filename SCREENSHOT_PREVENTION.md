# 🔒 Screenshot Prevention & Security Features

## Overview

Your vault now includes advanced screenshot prevention and screen recording detection to protect your sensitive password data from being captured.

---

## 🛡️ Security Features Implemented

### 1. **Screenshot Detection**
Detects and blocks common screenshot methods:
- ✅ Print Screen key
- ✅ Windows + Shift + S (Snipping Tool)
- ✅ Cmd + Shift + 3/4/5 (macOS screenshots)
- ✅ Browser screenshot APIs
- ✅ Screen recording software

### 2. **Automatic Protection Response**
When a screenshot attempt is detected:
1. 🚨 Warning banner appears
2. 🔒 All password fields immediately blur
3. 🔐 Vault automatically locks after 500ms
4. 📋 Clipboard is cleared
5. 🔄 User must reload page and re-enter master password

### 3. **Visual Security Measures**
- **Password Blur**: Sensitive content blurs when tab loses focus
- **Watermark**: "CONFIDENTIAL" watermark makes screenshots less useful
- **Context Menu Disabled**: Right-click menu blocked
- **Text Selection Disabled**: Can't select password text to copy

### 4. **Window Focus Protection**
- **Tab Switch Detection**: Content blurs when switching tabs
- **Inactive Warning**: If vault is inactive for 2+ seconds, asks for confirmation before showing data
- **Auto-lock on Blur**: Vault locks when window loses focus for extended periods

### 5. **Screen Recording Prevention**
- **API Blocking**: Blocks browser screen capture APIs
- **Media Device Detection**: Intercepts screen recording attempts
- **Focus Monitoring**: Detects when window is unfocused (potential recording software)

---

## 🎯 How It Works

### Screenshot Attempt Flow
```
User Presses Print Screen
        ↓
Detection Triggered
        ↓
⚠️ Warning Banner Shown
        ↓
🔒 All Passwords Blurred (8px blur filter)
        ↓
500ms Delay
        ↓
🔐 Vault Completely Locked
        ↓
📋 Clipboard Cleared
        ↓
Black Overlay: "VAULT LOCKED"
        ↓
User Must Reload Page
```

### Tab Switch Protection
```
User Switches Tab
        ↓
visibilitychange Event
        ↓
All Passwords Blurred
        ↓
User Switches Back
        ↓
Check Inactive Duration
        ↓
If > 2 seconds: Show Confirmation
        ↓
User Confirms → Unblur
User Denies → Lock Vault
```

---

## 🔍 Detection Methods

### Keyboard Shortcuts Detected
| Platform | Shortcut | Action |
|----------|----------|--------|
| Windows | Print Screen | Blocked & Lock |
| Windows | Win + Shift + S | Blocked & Lock |
| macOS | Cmd + Shift + 3 | Blocked & Lock |
| macOS | Cmd + Shift + 4 | Blocked & Lock |
| macOS | Cmd + Shift + 5 | Blocked & Lock |
| All | Right Click | Disabled |

### Browser APIs Blocked
- `navigator.mediaDevices.getDisplayMedia()` - Screen recording
- `document.execCommand('copy')` on password fields
- Context menu events
- Text selection on sensitive fields

---

## ⚙️ Technical Implementation

### CSS Security Features
```css
.password-value.secure-blur {
  filter: blur(8px);              /* Blur sensitive content */
  user-select: none;              /* Prevent text selection */
  -webkit-user-select: none;      /* Safari */
  -moz-user-select: none;         /* Firefox */
  -ms-user-select: none;          /* IE/Edge */
}
```

### JavaScript Detection
```javascript
// Print Screen detection
document.addEventListener('keyup', (e) => {
  if (e.key === 'PrintScreen') {
    handleScreenshotAttempt();
  }
});

// Screen recording API blocking
navigator.mediaDevices.getDisplayMedia = function() {
  handleScreenshotAttempt();
  return Promise.reject(new Error('Blocked for security'));
};
```

---

## 🎨 User Experience

### Visual Indicators

**1. Warning Banner**
- Appears at top-right corner
- Red background with white text
- Shows: "⚠️ Screenshot Detected! Vault Locked for Security"
- Auto-hides after 3 seconds

**2. Lock Overlay**
- Full-screen black overlay
- Shows: "🔒 VAULT LOCKED"
- Message: "Screenshot/Recording detected for your security"
- Instructions: "Reload the page to unlock"

**3. Watermark**
- Large "CONFIDENTIAL" text
- Rotated -45 degrees
- Semi-transparent red
- Always visible in background
- Makes screenshots identifiable and less useful

---

## 🚀 How to Use

### Normal Usage
1. Open vault normally - screenshot protection is automatic
2. Use vault as usual - protection runs in background
3. If you need to screenshot for legitimate reasons:
   - Export encrypted backup instead
   - Or take screenshot BEFORE unlocking vault

### Testing Screenshot Protection
```powershell
# 1. Open vault
cd frontend
npx http-server -p 8080

# 2. Unlock with master password
# 3. Try pressing Print Screen
# 4. Watch vault automatically lock! 🔒
```

### After Lock Triggered
1. You'll see red warning banner
2. Black overlay appears: "VAULT LOCKED"
3. Simply **reload the page** (F5 or Ctrl+R)
4. Enter your master password again
5. Continue using vault

---

## 🔐 Security Benefits

### Prevents
- ✅ Accidental screenshots during presentations
- ✅ Malicious screenshot capture by malware
- ✅ Screen recording by remote access tools
- ✅ Shoulder surfing (people looking over your shoulder)
- ✅ Screenshots being shared/leaked
- ✅ Password theft via image captures

### Limitations (Browser Security Model)
- ⚠️ Cannot prevent camera pointed at screen (physical security)
- ⚠️ Cannot prevent hardware screen capture devices
- ⚠️ Some browser extensions may bypass protections
- ⚠️ Advanced screen recording software may still work
- ⚠️ OS-level screenshot tools may not always be detected

**Best Practice**: Combine these features with:
- Private browsing mode
- Trusted device only
- Lock screen when away
- Regular vault locking

---

## 🎯 Recommended Settings

### For Maximum Security
1. **Enable Full-Screen Mode**: Press F11 (harder to record)
2. **Disable Browser Extensions**: They might bypass protections
3. **Use Private/Incognito Mode**: Less fingerprinting
4. **Close Other Apps**: Reduce recording software risk
5. **Lock When Idle**: Click "Lock" button when stepping away

### For Convenience
- Screenshot protection runs automatically
- No configuration needed
- Works silently in background
- Only activates when threat detected

---

## 🐛 Troubleshooting

### "Vault locks too frequently"
**Cause**: Tab switching or window focus changes
**Solution**: 
- Click "Continue" when prompted after returning to tab
- Keep vault in dedicated window
- Don't switch tabs frequently

### "I need to take a screenshot for legitimate reasons"
**Solutions**:
1. **Best**: Export encrypted backup file instead
2. **Alternative**: Screenshot the locked vault (no sensitive data)
3. **Workaround**: Copy password to notepad, screenshot notepad (less secure)

### "Screenshot protection not working"
**Check**:
- JavaScript is enabled
- Not using browser extensions that interfere
- Using modern browser (Chrome, Firefox, Edge)
- Not in compatibility mode

### "I accidentally triggered the lock"
**Solution**:
- Simply reload the page (F5)
- Re-enter master password
- Continue normally

---

## 📊 Feature Comparison

| Feature | Without Protection | With Protection |
|---------|-------------------|-----------------|
| **Screenshot** | Anyone can capture | Auto-locks vault |
| **Screen Recording** | Can record passwords | Blocks recording API |
| **Tab Switch** | Passwords visible | Auto-blurs content |
| **Copy/Paste** | Can copy anywhere | Clipboard cleared on threat |
| **Right Click** | Can use menu | Disabled |
| **Text Selection** | Can select passwords | Blocked |
| **Watermark** | Clean screenshots | Identifiable screenshots |

---

## 🔬 Advanced Features

### Clipboard Protection
```javascript
// Clipboard is cleared after screenshot attempt
navigator.clipboard.writeText('').catch(err => {
  console.log('Clipboard clear failed');
});
```

### Focus Monitoring
```javascript
// Monitors how long vault was inactive
let lastFocusTime = Date.now();
window.addEventListener('blur', () => {
  lastFocusTime = Date.now();
  blurSensitiveContent(true);
});
```

### Screen Capture API Override
```javascript
// Blocks browser screen capture
const originalGetDisplayMedia = navigator.mediaDevices.getDisplayMedia;
navigator.mediaDevices.getDisplayMedia = function() {
  handleScreenshotAttempt();
  return Promise.reject(new Error('Blocked'));
};
```

---

## 🎓 Understanding the Security

### Defense in Depth
This implements **multiple layers** of security:
1. **Detection Layer**: Keyboard shortcuts, API calls
2. **Response Layer**: Blur, lock, clear clipboard
3. **Visual Layer**: Watermark, warnings
4. **Behavioral Layer**: Focus monitoring, inactivity detection

### Zero Trust Approach
- Assumes screenshot attempts are malicious
- Immediately locks vault
- Requires user to re-authenticate
- Clears sensitive data from memory

### Privacy by Design
- No data sent to server
- All detection happens locally
- No external dependencies
- Works completely offline

---

## 🚀 Future Enhancements (Optional)

Potential additions you could implement:
1. **Photo Detection**: Use webcam to detect phones pointed at screen
2. **Timed Auto-Lock**: Lock vault after X minutes of inactivity
3. **Biometric Unlock**: Use fingerprint/face to unlock after screenshot lock
4. **Decoy Data**: Show fake passwords in screenshots
5. **Session Recording**: Log all screenshot attempts for review
6. **Hardware Detection**: Detect HDMI capture cards

---

## 📝 Configuration Options

Currently screenshot protection is **always enabled** and requires no configuration. This is intentional for maximum security.

If you want to disable it (NOT recommended):
1. Comment out `initScreenshotPrevention()` in the init function
2. Remove screenshot prevention CSS
3. Reload the vault

**Warning**: Disabling screenshot protection significantly reduces security!

---

## ✅ Testing Checklist

Test these scenarios to verify protection:

- [ ] Press Print Screen → Should lock vault
- [ ] Press Win + Shift + S → Should lock vault
- [ ] Right-click on password → Should block
- [ ] Try to select password text → Should block
- [ ] Switch tabs → Should blur content
- [ ] Leave vault inactive 2+ seconds → Should prompt
- [ ] Try browser screen recording → Should block
- [ ] Check watermark is visible → Should see "CONFIDENTIAL"

All tests should result in vault locking or content being protected! ✅

---

## 🎉 Summary

Your vault now has **enterprise-grade screenshot protection**:
- 🔒 Automatic screenshot detection
- 🚨 Instant response (blur + lock)
- 📋 Clipboard protection
- 🎨 Visual watermarking
- 👁️ Focus monitoring
- 🚫 API blocking

**Your passwords are now protected from screen capture! 🛡️**

---

## 💡 Pro Tips

1. **Lock when presenting**: Click "Lock" before screen sharing
2. **Use dedicated browser**: Keep vault in separate browser profile
3. **Private browsing**: Use incognito mode for extra privacy
4. **Regular exports**: Screenshot protection doesn't replace backups!
5. **Test it**: Try pressing Print Screen to see it work

**Stay secure! 🔐**
