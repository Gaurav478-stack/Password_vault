// Generate secure secrets for Render deployment
const crypto = require('crypto');

console.log('\n🔐 SecurePass Vault - Secret Generator for Render Deployment\n');
console.log('Copy these values to Render Environment Variables:\n');
console.log('═'.repeat(70));

const jwtSecret = crypto.randomBytes(32).toString('hex');
const secretKey = crypto.randomBytes(32).toString('hex');

console.log('\nJWT_SECRET:');
console.log(jwtSecret);

console.log('\nSECRET_KEY:');
console.log(secretKey);

console.log('\n' + '═'.repeat(70));
console.log('\n📋 Instructions:');
console.log('1. Go to Render Dashboard → Your Web Service → Environment');
console.log('2. Add environment variables with these values');
console.log('3. Add your MONGO_URI from MongoDB Atlas');
console.log('4. Save and redeploy\n');
console.log('✅ Keep these secrets secure! Never commit them to Git.\n');
