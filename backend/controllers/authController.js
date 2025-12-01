const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const User = require("../models/User");

const SALT_ROUNDS = parseInt(process.env.SALT_ROUNDS || "10", 10);

exports.register = async (req, res) => {
    if (
        process.env.NODE_ENV === "production" &&
        process.env.ENABLE_REGISTER !== "true"
    ) {
        return res.status(403).json({ error: "Register disabled" });
    }

    const { username, password } = req.body;
    const errors = [];
    if (!username || typeof username !== "string")
        errors.push({ field: "username", message: "Username is required" });
    if (!password || typeof password !== "string" || password.length < 6)
        errors.push({
            field: "password",
            message: "Password must be at least 6 characters",
        });
    if (errors.length) return res.status(400).json({ errors });

    const existing = await User.findOne({ username });
    if (existing)
        return res.status(400).json({
            errors: [{ field: "username", message: "Username already exists" }],
        });

    const passwordHash = await bcrypt.hash(password, SALT_ROUNDS);
    const user = await User.create({ username, passwordHash });
    return res.status(201).json({ id: user._id, username: user.username });
};

exports.login = async (req, res) => {
    const { username, password } = req.body;
    if (!username || !password)
        return res
            .status(400)
            .json({ errors: [{ message: "username and password required" }] });

    const user = await User.findOne({ username });
    if (!user)
        return res
            .status(401)
            .json({ errors: [{ message: "Invalid credentials" }] });

    const ok = await bcrypt.compare(password, user.passwordHash);
    if (!ok)
        return res
            .status(401)
            .json({ errors: [{ message: "Invalid credentials" }] });

    const accessToken = jwt.sign(
        { id: user._id, username: user.username },
        process.env.JWT_SECRET,
        {
            expiresIn: process.env.JWT_EXPIRES_IN || "15m",
        }
    );

    const refreshToken = jwt.sign(
        { id: user._id, username: user.username },
        process.env.JWT_REFRESH_SECRET || process.env.JWT_SECRET,
        {
            expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || "30d",
        }
    );

    // store refresh token
    user.refreshTokens = user.refreshTokens || [];
    user.refreshTokens.push(refreshToken);
    await user.save();

    return res.json({
        token: accessToken,
        refreshToken,
        user: { id: user._id, username: user.username },
    });
};

exports.refresh = async (req, res) => {
    const { refreshToken } = req.body;
    if (!refreshToken)
        return res
            .status(400)
            .json({ errors: [{ message: "refreshToken required" }] });

    let payload;
    try {
        payload = jwt.verify(
            refreshToken,
            process.env.JWT_REFRESH_SECRET || process.env.JWT_SECRET
        );
    } catch (err) {
        return res
            .status(401)
            .json({
                errors: [{ message: "Invalid or expired refresh token" }],
            });
    }

    const user = await User.findOne({
        _id: payload.id,
        refreshTokens: refreshToken,
    });
    if (!user)
        return res
            .status(401)
            .json({ errors: [{ message: "Refresh token not recognized" }] });

    // rotate refresh token
    const newAccessToken = jwt.sign(
        { id: user._id, username: user.username },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN || "15m" }
    );

    const newRefreshToken = jwt.sign(
        { id: user._id, username: user.username },
        process.env.JWT_REFRESH_SECRET || process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_REFRESH_EXPIRES_IN || "30d" }
    );

    user.refreshTokens = user.refreshTokens.filter((t) => t !== refreshToken);
    user.refreshTokens.push(newRefreshToken);
    await user.save();

    return res.json({ token: newAccessToken, refreshToken: newRefreshToken });
};

exports.logout = async (req, res) => {
    const { refreshToken } = req.body;
    if (!refreshToken)
        return res
            .status(400)
            .json({ errors: [{ message: "refreshToken required" }] });

    const user = await User.findOne({ refreshTokens: refreshToken });
    if (!user) return res.status(200).json({}); // already removed

    user.refreshTokens = user.refreshTokens.filter((t) => t !== refreshToken);
    await user.save();
    return res.status(200).json({});
};
