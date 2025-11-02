// Test script for cryptoUtils
const { encrypt, decrypt } = require('./backend/cryptoUtils');

console.log('Testing AES-256-GCM encryption/decryption...\n');

const testCases = [
    { text: 'MyPassword123!', secret: 'test-secret-key' },
    { text: 'AnotherP@ssw0rd', secret: 'different-secret' },
    { text: 'Short', secret: 'key123' },
    { text: 'Very long password with special characters !@#$%^&*()_+-={}[]|\\:";\'<>?,./~`', secret: 'strong-secret-key' }
];

let allPassed = true;

testCases.forEach((testCase, index) => {
    console.log(`Test ${index + 1}:`);
    console.log(`  Original: "${testCase.text}"`);
    
    try {
        // Encrypt
        const encrypted = encrypt(testCase.text, testCase.secret);
        console.log(`  Encrypted: ${encrypted.substring(0, 50)}...`);
        
        // Decrypt
        const decrypted = decrypt(encrypted, testCase.secret);
        console.log(`  Decrypted: "${decrypted}"`);
        
        // Verify
        if (decrypted === testCase.text) {
            console.log(`  ✅ PASSED\n`);
        } else {
            console.log(`  ❌ FAILED: Decrypted text doesn't match\n`);
            allPassed = false;
        }
    } catch (err) {
        console.log(`  ❌ FAILED: ${err.message}\n`);
        allPassed = false;
    }
});

// Test with wrong key (should fail)
console.log('Test: Decryption with wrong key (should fail)');
try {
    const encrypted = encrypt('test', 'correct-key');
    const decrypted = decrypt(encrypted, 'wrong-key');
    console.log('  ❌ FAILED: Should have thrown an error\n');
    allPassed = false;
} catch (err) {
    console.log('  ✅ PASSED: Correctly failed with wrong key\n');
}

// Test with invalid format (should fail)
console.log('Test: Decryption with invalid format (should fail)');
try {
    decrypt('invalid:format', 'some-key');
    console.log('  ❌ FAILED: Should have thrown an error\n');
    allPassed = false;
} catch (err) {
    console.log('  ✅ PASSED: Correctly failed with invalid format\n');
}

console.log('═══════════════════════════════════════');
if (allPassed) {
    console.log('✅ All tests passed!');
} else {
    console.log('❌ Some tests failed!');
    process.exit(1);
}
