import express from "express";
import multer from "multer";
import fs from "node:fs/promises";

import * as ScamService from "./ScamService.js";
import { measureMemory } from "node:vm";

const router = express.Router();

router.get("/", (req, res) => {
    res.render("init");
});

export default router;
