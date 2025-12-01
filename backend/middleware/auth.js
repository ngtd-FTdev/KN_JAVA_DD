const jwt = require("jsonwebtoken");

function authMiddleware(req, res, next) {
    const auth = req.headers.authorization;
    if (!auth || !auth.startsWith("Bearer ")) {
        return res.status(401).json({ error: "Authorization header missing" });
    }

    const token = auth.slice(7);
    try {
        const payload = jwt.verify(token, process.env.JWT_SECRET);
        req.user = { id: payload.id, username: payload.username };
        next();
    } catch (err) {
        // If token is expired, return a specific error so clients can trigger refresh
        if (err && err.name === "TokenExpiredError") {
            return res
                .status(401)
                .json({
                    error: "token_expired",
                    message: "Access token expired",
                });
        }
        return res
            .status(401)
            .json({ error: "invalid_token", message: "Invalid token" });
    }
}

module.exports = authMiddleware;
