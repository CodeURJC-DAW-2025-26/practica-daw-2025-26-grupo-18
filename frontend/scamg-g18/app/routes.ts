import { type RouteConfig, index, layout } from "@react-router/dev/routes";

export default [
  layout("routes/home.tsx", [
    index("components/index.tsx")
  ])
] satisfies RouteConfig;
