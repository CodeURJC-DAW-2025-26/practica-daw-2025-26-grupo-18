import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
  layout("routes/home.tsx", [
    index("components/index.tsx"),
    route("cart", "routes/cart.tsx")
  ])
] satisfies RouteConfig;
