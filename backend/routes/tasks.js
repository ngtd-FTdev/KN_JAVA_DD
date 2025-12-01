const express = require("express");
const router = express.Router();
const asyncHandler = require("../utils/asyncHandler");
const taskController = require("../controllers/taskController");
const { createTask, updateTask } = require("../validators/taskValidator");
const { validateRequest } = require("../validators/validateRequest");

router.get("/", asyncHandler(taskController.listTasks));
router.post(
    "/",
    createTask,
    validateRequest,
    asyncHandler(taskController.createTask)
);
router.get("/:id", asyncHandler(taskController.getTask));
router.put(
    "/:id",
    updateTask,
    validateRequest,
    asyncHandler(taskController.updateTask)
);
router.delete("/:id", asyncHandler(taskController.deleteTask));

module.exports = router;
