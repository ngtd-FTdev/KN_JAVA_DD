const { body } = require("express-validator");

exports.createTask = [
    body("title")
        .trim()
        .notEmpty()
        .withMessage("Title is required")
        .isLength({ max: 200 })
        .withMessage("Title must be at most 200 characters"),
    body("description")
        .optional()
        .isString()
        .withMessage("Description must be a string"),
    body("dueDate")
        .optional()
        .isISO8601()
        .withMessage("dueDate must be a valid ISO8601 date"),
];

exports.updateTask = [
    body("title")
        .optional()
        .trim()
        .notEmpty()
        .withMessage("If provided, title cannot be empty"),
    body("description").optional().isString(),
    body("completed")
        .optional()
        .isBoolean()
        .withMessage("Completed must be boolean"),
];
