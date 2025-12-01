const mongoose = require("mongoose");

const taskSchema = new mongoose.Schema(
    {
        title: { type: String, required: true, minlength: 1, maxlength: 200 },
        description: { type: String, maxlength: 2000 },
        completed: { type: Boolean, default: false },
        owner: {
            type: mongoose.Schema.Types.ObjectId,
            ref: "User",
            required: true,
            index: true,
        },
    },
    { timestamps: true }
);

taskSchema.index({ owner: 1, createdAt: -1 });
taskSchema.index({ owner: 1, title: 1 });

module.exports = mongoose.model("Task", taskSchema);
