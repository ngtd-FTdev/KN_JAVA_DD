const { body } = require("express-validator");

exports.registerValidator = [
    body("name").trim().notEmpty().withMessage("Name is required"),
    body("username")
        .trim()
        .notEmpty()
        .withMessage("username is required")
        .withMessage("username must be valid"),
    body("password")
        .notEmpty()
        .withMessage("Password is required")
        .isLength({ min: 6 })
        .withMessage("Password must be at least 6 characters"),
];

exports.loginValidator = [
    body("username")
        .trim()
        .notEmpty()
        .withMessage("username is required")
        .withMessage("username must be valid"),
    body("password").notEmpty().withMessage("Password is required"),
];

exports.refreshValidator = [
    body("refreshToken").notEmpty().withMessage("refreshToken is required"),
];

exports.logoutValidator = [
    body("refreshToken").notEmpty().withMessage("refreshToken is required"),
];
