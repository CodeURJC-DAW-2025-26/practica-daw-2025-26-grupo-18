import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  route("new", "routes/home.tsx", [
    index("components/index.tsx"),
    route("admin", "routes/admin.tsx"),
    route("profile/me", "routes/profile.me.tsx"),
    route("profile/:id", "routes/profile.$id.tsx"),
    route("cart", "routes/cart.tsx")
  ])
] satisfies RouteConfig;
