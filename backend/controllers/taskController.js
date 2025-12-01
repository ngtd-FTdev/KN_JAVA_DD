const Task = require("../models/Task");

exports.listTasks = async (req, res) => {
    const userId = req.user.id;
    const { page = 1, limit = 20, completed, q, sort } = req.query;
    const pageNum = Math.max(1, parseInt(page, 10) || 1);
    const limitNum = Math.min(100, Math.max(1, parseInt(limit, 10) || 20));

    const filter = { owner: userId };
    if (typeof completed !== "undefined") {
        if (completed === "true") filter.completed = true;
        else if (completed === "false") filter.completed = false;
    }

    if (q) {
        const re = new RegExp(q.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"), "i");
        filter.$or = [{ title: re }, { description: re }];
    }

    let sortObj = { createdAt: -1 };
    if (sort) {
        const key = sort.startsWith("-") ? sort.slice(1) : sort;
        const dir = sort.startsWith("-") ? -1 : 1;
        sortObj = { [key]: dir };
    }

    const total = await Task.countDocuments(filter);
    const data = await Task.find(filter)
        .sort(sortObj)
        .skip((pageNum - 1) * limitNum)
        .limit(limitNum)
        .lean();

    res.json({ data, meta: { page: pageNum, limit: limitNum, total } });
};

exports.createTask = async (req, res) => {
    const { title, description } = req.body;
    const errors = [];
    if (!title || typeof title !== "string" || title.trim().length === 0)
        errors.push({ field: "title", message: "Title is required" });
    if (description && description.length > 2000)
        errors.push({ field: "description", message: "Description too long" });
    if (errors.length) return res.status(400).json({ errors });

    const task = await Task.create({
        title: title.trim(),
        description: description || "",
        owner: req.user.id,
    });
    return res.status(201).json(task);
};

exports.getTask = async (req, res) => {
    const { id } = req.params;
    const task = await Task.findById(id).lean();
    if (!task) return res.status(404).json({ error: "Not found" });
    if (String(task.owner) !== String(req.user.id))
        return res.status(403).json({ error: "Forbidden" });
    return res.json(task);
};

exports.updateTask = async (req, res) => {
    const { id } = req.params;
    const updates = {};
    const allowed = ["title", "description", "completed"];
    for (const k of allowed) {
        if (typeof req.body[k] !== "undefined") updates[k] = req.body[k];
    }

    if (
        updates.title !== undefined &&
        (!updates.title || updates.title.trim().length === 0)
    ) {
        return res
            .status(400)
            .json({
                errors: [{ field: "title", message: "Title cannot be empty" }],
            });
    }

    const task = await Task.findById(id);
    if (!task) return res.status(404).json({ error: "Not found" });
    if (String(task.owner) !== String(req.user.id))
        return res.status(403).json({ error: "Forbidden" });

    if (updates.title !== undefined) task.title = updates.title;
    if (updates.description !== undefined)
        task.description = updates.description;
    if (updates.completed !== undefined) task.completed = updates.completed;

    await task.save();
    return res.json(task);
};

exports.deleteTask = async (req, res) => {
    const { id } = req.params;
    const task = await Task.findById(id);
    if (!task) return res.status(404).json({ error: "Not found" });
    if (String(task.owner) !== String(req.user.id))
        return res.status(403).json({ error: "Forbidden" });

    await task.deleteOne();
    return res.json({ msg: "Deleted" });
};
