const express = require("express");
const router = express.Router();
const asyncHandler = require("../utils/asyncHandler");
const authController = require("../controllers/authController");
const {
    registerValidator,
    loginValidator,
} = require("../validators/authValidator");
const { validateRequest } = require("../validators/validateRequest");

router.post(
    "/register",
    registerValidator,
    validateRequest,
    asyncHandler(authController.register)
);
router.post(
    "/login",
    loginValidator,
    validateRequest,
    asyncHandler(authController.login)
);

router.post(
    "/refresh",
    require("../validators/authValidator").refreshValidator,
    validateRequest,
    asyncHandler(authController.refresh)
);

router.post(
    "/logout",
    require("../validators/authValidator").logoutValidator,
    validateRequest,
    asyncHandler(authController.logout)
);

module.exports = router;
