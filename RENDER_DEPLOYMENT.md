# 🚀 Render Deployment Guide

This guide will help you deploy the SecurePass Vault backend API to Render.

## 📋 Prerequisites

1. **Render Account**: Sign up at https://render.com (free tier available)
2. **MongoDB Atlas Account**: Sign up at https://www.mongodb.com/cloud/atlas (free tier available)
3. **GitHub Repository**: Your code should be pushed to GitHub

## 🔧 Step 1: Set Up MongoDB Atlas

1. Go to https://www.mongodb.com/cloud/atlas
2. Create a **free cluster** (M0 Sandbox)
3. **Create a database user**:
   - Go to Database Access → Add New Database User
   - Username: `securepassUser`
   - Password: (generate a strong password, save it!)
4. **Whitelist all IPs** (required for Render):
   - Go to Network Access → Add IP Address
   - Click "Allow Access from Anywhere" → `0.0.0.0/0`
   - Confirm
5. **Get connection string**:
   - Go to Database → Connect → Connect your application
   - Copy the connection string (looks like: `mongodb+srv://securepassUser:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority`)
   - Replace `<password>` with your actual password
   - Add database name: `mongodb+srv://securepassUser:<password>@cluster0.xxxxx.mongodb.net/securepass?retryWrites=true&w=majority`

## 🌐 Step 2: Deploy to Render

### Option A: Using render.yaml (Automatic)

1. **Push render.yaml to GitHub**:
   ```bash
   git add render.yaml
   git commit -m "Add Render configuration"
   git push origin master
   ```

2. **Create New Web Service on Render**:
   - Go to https://dashboard.render.com/
   - Click **"New +"** → **"Web Service"**
   - Connect your GitHub account and select your repository
   - Render will detect `render.yaml` automatically

3. **Configure Environment Variables**:
   Render will prompt you for these values:
   
   | Variable | Value | How to Generate |
   |----------|-------|-----------------|
   | `MONGO_URI` | Your MongoDB Atlas connection string | From Step 1 |
   | `JWT_SECRET` | Random string (32+ chars) | `node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"` |
   | `SECRET_KEY` | Random string (32+ chars) | `node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"` |

4. **Deploy**:
   - Click "Create Web Service"
   - Render will build and deploy automatically
   - Wait 3-5 minutes for deployment

### Option B: Manual Setup

1. **Create New Web Service**:
   - Go to https://dashboard.render.com/
   - Click **"New +"** → **"Web Service"**
   - Connect your GitHub repository

2. **Configure Build Settings**:
   - **Name**: `securepass-vault-api`
   - **Region**: Oregon (or closest to you)
   - **Branch**: `master`
   - **Root Directory**: Leave empty
   - **Environment**: `Node`
   - **Build Command**: `cd backend && npm install`
   - **Start Command**: `cd backend && npm start`
   - **Instance Type**: Free

3. **Add Environment Variables**:
   Click "Advanced" → "Add Environment Variable":
   ```
   NODE_ENV=production
   PORT=10000
   MONGO_URI=mongodb+srv://securepassUser:<password>@cluster0.xxxxx.mongodb.net/securepass?retryWrites=true&w=majority
   JWT_SECRET=(run: node -e "console.log(require('crypto').randomBytes(32).toString('hex'))")
   SECRET_KEY=(run: node -e "console.log(require('crypto').randomBytes(32).toString('hex'))")
   ```

4. **Deploy**: Click "Create Web Service"

## ✅ Step 3: Verify Deployment

Once deployed, your API will be available at:
```
https://securepass-vault-api.onrender.com
```

### Test Health Endpoint

```bash
curl https://your-app-name.onrender.com/health
```

Expected response:
```json
{
  "status": "healthy",
  "database": "connected",
  "uptime": 123.456
}
```

### Test API Endpoints

1. **Register User**:
   ```bash
   curl -X POST https://your-app-name.onrender.com/api/register \
     -H "Content-Type: application/json" \
     -d '{
       "username": "testuser",
       "email": "test@example.com",
       "authPassword": "strongPassword123!"
     }'
   ```

2. **Login**:
   ```bash
   curl -X POST https://your-app-name.onrender.com/api/login \
     -H "Content-Type: application/json" \
     -d '{
       "username": "testuser",
       "authPassword": "strongPassword123!"
     }'
   ```

## 🔗 Step 4: Connect Frontend to Render API

Update your frontend code to use the Render API URL:

In `frontend/script.js` or wherever you make API calls:

```javascript
// Replace localhost with your Render URL
const API_BASE_URL = 'https://your-app-name.onrender.com/api';

// Example API call
async function registerUser(username, email, authPassword) {
    const response = await fetch(`${API_BASE_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, email, authPassword })
    });
    return response.json();
}
```

## 🎯 Common Issues & Solutions

### Issue: "Application failed to respond"
- **Solution**: Check logs in Render dashboard → Logs tab
- Verify all environment variables are set correctly
- Ensure MongoDB connection string is correct

### Issue: "Module not found"
- **Solution**: Verify `package.json` is in `/backend` folder
- Check build command: `cd backend && npm install`

### Issue: MongoDB connection timeout
- **Solution**: 
  - Verify IP whitelist includes `0.0.0.0/0`
  - Check MongoDB user credentials
  - Ensure connection string format is correct

### Issue: Free tier sleeps after inactivity
- **Solution**: 
  - Render free tier sleeps after 15 minutes of inactivity
  - First request after sleep takes 30-60 seconds
  - Upgrade to paid plan for always-on service
  - Or use a service like UptimeRobot to ping your API every 10 minutes

## 🔄 Auto-Deploy on Git Push

Render automatically redeploys when you push to your GitHub branch:

```bash
git add .
git commit -m "Update backend"
git push origin master
# Render will automatically detect and deploy
```

## 📊 Monitor Your Deployment

- **Dashboard**: https://dashboard.render.com/
- **Logs**: View real-time logs in the Render dashboard
- **Metrics**: See CPU, memory usage, and request metrics

## 🔒 Security Best Practices

1. ✅ **Use strong secrets**: Generate with `crypto.randomBytes(32).toString('hex')`
2. ✅ **Never commit .env files**: Already in `.gitignore`
3. ✅ **Use environment variables**: Set in Render dashboard, not in code
4. ✅ **Enable HTTPS**: Automatic on Render
5. ✅ **Restrict MongoDB access**: Use strong passwords and IP whitelisting

## 💰 Pricing

- **Render Free Tier**:
  - 750 hours/month
  - Sleeps after 15 min inactivity
  - 512 MB RAM
  - 0.1 CPU
  - Perfect for demos and testing

- **Render Starter ($7/month)**:
  - Always on (no sleeping)
  - 512 MB RAM
  - 0.5 CPU
  - Better for production

## 📚 Additional Resources

- Render Documentation: https://render.com/docs
- MongoDB Atlas Docs: https://docs.atlas.mongodb.com/
- Node.js Best Practices: https://github.com/goldbergyoni/nodebestpractices

## 🎉 Success!

Your SecurePass Vault API is now live on Render! 🚀

**API URL**: `https://your-app-name.onrender.com`

Share this URL with your frontend or use it in API testing tools like Postman.

---

Need help? Check the Render logs or open an issue on GitHub.
