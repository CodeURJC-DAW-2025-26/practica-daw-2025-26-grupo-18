import Footer from "~/components/Footer";
import { Outlet } from "react-router";
import Header from "~/components/Header";
import { useEffect } from "react";

export default function Home() {
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
