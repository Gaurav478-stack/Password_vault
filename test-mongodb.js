// Test MongoDB connection
require('dotenv').config();
const mongoose = require('mongoose');

const MONGO_URI = process.env.MONGO_URI || 'YOUR_CONNECTION_STRING_HERE';

console.log('🔌 Testing MongoDB connection...\n');
console.log('Connection string:', MONGO_URI.replace(/:[^:@]+@/, ':****@')); // Hide password

mongoose.connect(MONGO_URI)
    .then(() => {
        console.log('✅ MongoDB connection successful!');
        console.log('📊 Database:', mongoose.connection.name);
        console.log('🌍 Host:', mongoose.connection.host);
        process.exit(0);
    })
    .catch(err => {
        console.error('❌ MongoDB connection failed:');
        console.error(err.message);
        console.log('\n💡 Common fixes:');
        console.log('1. Check your password is correct (no < or > brackets)');
        console.log('2. Ensure IP whitelist includes 0.0.0.0/0 in MongoDB Atlas');
        console.log('3. Verify the connection string format');
        process.exit(1);
    });
