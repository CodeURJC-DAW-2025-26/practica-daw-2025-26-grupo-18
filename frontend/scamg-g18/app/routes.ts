import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  route("new", "routes/home.tsx", [
    index("components/index.tsx"),
    route("login", "routes/login.tsx"),
    route("register", "routes/register.tsx"),
    route("admin", "routes/admin.tsx"),
    route("profile/me", "routes/profile.me.tsx"),
    route("profile/:id", "routes/profile.$id.tsx"),
    route("cart", "routes/cart.tsx"),
    
    // Courses
    route("courses", "routes/courses.tsx"),
    route("courses/new", "routes/courses.new.tsx"),
    route("courses/:id", "routes/courses.$id.tsx"),
    route("courses/:id/edit", "routes/courses.$id.edit.tsx"),
    
    // Events
    route("events", "routes/events.tsx"),
    route("events/new", "routes/events.new.tsx"),
    route("events/:id", "routes/events.$id.tsx"),
    route("events/:id/edit", "routes/events.$id.edit.tsx"),
  ])
] satisfies RouteConfig;
