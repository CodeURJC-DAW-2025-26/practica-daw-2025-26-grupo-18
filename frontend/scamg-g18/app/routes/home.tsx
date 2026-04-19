import Footer from "~/components/Footer";
import type { Route } from "./+types/home";
import { Outlet } from "react-router";
import Header from "~/components/Header";

export function loader() {
    return { name: "React Router" };
}

export default function Home({ loaderData }: Route.ComponentProps) {
    return (
        <div className="d-flex flex-column min-vh-100">
            <Header />
            <main className="flex-grow-1">
                <Outlet />
            </main>
            <Footer />
        </div>
    );
}
