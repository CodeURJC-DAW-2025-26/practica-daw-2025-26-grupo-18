import Footer from "~/components/Footer";
import type { Route } from "./+types/home";
import { Outlet } from "react-router";
import Header from "~/components/Header";
import { useEffect } from "react";

export function loader() {
    return { name: "React Router" };
}

export default function Home({ loaderData }: Route.ComponentProps) {
    useEffect(() => {
        document.body.classList.add("index-page");
        return () => {
            document.body.classList.remove("index-page");
            document.body.classList.remove("scrolled");
            document.body.classList.remove("mobile-nav-active");
        };
    }, []);

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
