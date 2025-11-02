const mongoose = require('mongoose');

const passwordSchema = new mongoose.Schema({
  service: String,
  url: String,
  username: String,
  password: String, // encrypted blob (iv:hex)
  category: String,
  icon: String,
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true }
}, { timestamps: true });

module.exports = mongoose.model('Password', passwordSchema);
