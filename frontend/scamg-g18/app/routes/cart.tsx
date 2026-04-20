import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router";
import type { CartDTO, CheckoutRequestDTO } from "../dtos/CartDTO";
import { getCart, removeItemFromCart, checkout } from "../services/cartService";

export default function Cart() {
    const [order, setOrder] = useState<CartDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const [errorNoSeats, setErrorNoSeats] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    
    // Checkout form state
    const [checkoutForm, setCheckoutForm] = useState<CheckoutRequestDTO>({
        cardName: "",
        billingEmail: "",
        cardNumber: "",
        cardExpiry: "",
        cardCvv: ""
    });

    const navigate = useNavigate();

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            setLoading(true);
            const cartData = await getCart();
            setOrder(cartData);
            setErrorMessage("");
        } catch (error: any) {
            if (error.message.includes("401")) {
                navigate("/login");
            } else {
                setErrorMessage("No se pudo cargar el carrito. Inténtalo más tarde.");
            }
        } finally {
            setLoading(false);
        }
    };

    const handleRemoveItem = async (itemId: number) => {
        try {
            const updatedOrder = await removeItemFromCart(itemId);
            setOrder(updatedOrder);
        } catch (error) {
            setErrorMessage("Error al eliminar el producto del carrito.");
        }
    };

    const handleCheckoutChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setCheckoutForm(prev => ({ ...prev, [name]: value }));
    };

    const handleCheckoutSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorNoSeats(false);
        setErrorMessage("");

        try {
            await checkout(checkoutForm);
            navigate("/"); // Redirect to home or success page after successful checkout
        } catch (error: any) {
            if (error.message.includes("EVENT_FULL") || error.message.includes("eventFull")) {
                setErrorNoSeats(true);
            } else {
                setErrorMessage(error.message || "Error al procesar el pago. Por favor, revisa los datos introducidos.");
            }
        }
    };

    // Calculate totals based on totalAmountCents (tax inclusive 21%)
    const totalEuros = order ? order.totalAmountCents / 100 : 0;
    const subtotalEuros = totalEuros / 1.21;
    const taxEuros = totalEuros - subtotalEuros;

    if (loading) {
        return (
            <main className="main">
                <div className="container py-5 text-center">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </div>
                </div>
            </main>
        );
    }

    return (
        <main className="main">
            {/* Page Title */}
            <div className="page-title light-background">
                <div className="container">
                    <nav className="breadcrumbs">
                        <ol>
                            <li><Link to="/">Inicio</Link></li>
                            <li className="current">Carrito</li>
                        </ol>
                    </nav>
                    <h1>Carrito de Compra</h1>
                </div>
            </div>
            {/* End Page Title */}

            {/* Cart Section */}
            <section id="cart-section" className="section">
                <div className="container" data-aos="fade-up">
                    {errorNoSeats && (
                        <div className="alert alert-danger" role="alert">
                            No se ha podido completar la compra porque uno de los eventos ya no tiene plazas disponibles.
                        </div>
                    )}

                    {errorMessage && (
                        <div className="alert mb-4" style={{ backgroundColor: "rgba(217, 109, 60, 0.15)", color: "#D96D3C", border: "1px solid #D96D3C" }} role="alert">
                            <i className="bi bi-exclamation-triangle-fill"></i> {errorMessage}
                        </div>
                    )}

                    {/* Back Button */}
                    <div className="mb-4">
                        <Link to="/" className="btn btn-secondary d-inline-flex align-items-center gap-2" style={{ backgroundColor: "#85613d", borderColor: "#85613d" }}>
                            <i className="bi bi-arrow-left"></i> Volver
                        </Link>
                    </div>

                    <div className="row">
                        <div className="col-lg-8">
                            {/* Cart Items */}
                            <div className="card shadow-sm mb-4">
                                <div className="card-header bg-white py-3">
                                    <h5 className="mb-0">Tu Pedido</h5>
                                </div>
                                <div className="card-body">
                                    <div className="table-responsive">
                                        <table className="table table-hover align-middle">
                                            <thead className="table-light">
                                                <tr>
                                                    <th scope="col">Producto</th>
                                                    <th scope="col">Cantidad</th>
                                                    <th scope="col" className="text-end">Precio</th>
                                                    <th scope="col" className="text-end">Acciones</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {order && order.items && order.items.length > 0 ? (
                                                    order.items.map((item) => (
                                                        <tr key={item.id}>
                                                            <td>
                                                                <div className="d-flex align-items-center">
                                                                    {/* Placeholder image */}
                                                                    <div className="ms-2">
                                                                        {item.course && (
                                                                            <>
                                                                                <h6 className="mb-0">{item.course.title}</h6>
                                                                                <small className="text-muted">Curso Online</small>
                                                                            </>
                                                                        )}
                                                                        {item.event && (
                                                                            <>
                                                                                <h6 className="mb-0">{item.event.title}</h6>
                                                                                <small className="text-muted">Evento Presencial</small>
                                                                            </>
                                                                        )}
                                                                        {item.subscription && (
                                                                            <>
                                                                                <h6 className="mb-0">Suscripción Premium</h6>
                                                                                <small className="text-muted">Acceso Mensual</small>
                                                                            </>
                                                                        )}
                                                                    </div>
                                                                </div>
                                                            </td>
                                                            <td>
                                                                1 {/* Fixed quantity for now for courses/events */}
                                                            </td>
                                                            <td className="text-end">
                                                                {item.priceInEuros}€
                                                            </td>
                                                            <td className="text-end">
                                                                <button 
                                                                    type="button" 
                                                                    className="btn btn-sm btn-outline-danger"
                                                                    onClick={() => handleRemoveItem(item.id)}
                                                                >
                                                                    Eliminar
                                                                </button>
                                                            </td>
                                                        </tr>
                                                    ))
                                                ) : (
                                                    <tr>
                                                        <td colSpan={4} className="text-center py-4">Tu carrito está vacío.</td>
                                                    </tr>
                                                )}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="col-lg-4">
                            {/* Order Summary */}
                            <div className="card shadow-sm">
                                <div className="card-header bg-white py-3">
                                    <h5 className="mb-0">Resumen</h5>
                                </div>
                                <div className="card-body">
                                    <ul className="list-group list-group-flush mb-3">
                                        <li className="list-group-item d-flex justify-content-between border-0 px-0 pb-0">
                                            Subtotal
                                            <span>{subtotalEuros.toFixed(2)}€</span>
                                        </li>
                                        <li className="list-group-item d-flex justify-content-between border-0 px-0">
                                            Impuestos (21%)
                                            <span>{taxEuros.toFixed(2)}€</span>
                                        </li>
                                        <li className="list-group-item d-flex justify-content-between border-0 px-0 mb-3">
                                            <strong>Total</strong>
                                            <strong>{totalEuros.toFixed(2)}€</strong>
                                        </li>
                                    </ul>

                                    {/* Pay Button */}
                                    <div className="d-grid gap-2">
                                        <button
                                            className="btn btn-primary btn-lg"
                                            type="button"
                                            style={{ backgroundColor: "#d96d3c", borderColor: "#d96d3c" }}
                                            data-bs-toggle="collapse"
                                            data-bs-target="#paymentForm"
                                            aria-expanded="false"
                                            aria-controls="paymentForm"
                                            disabled={!order || !order.items || order.items.length === 0}>
                                            Pagar Ahora
                                        </button>
                                    </div>
                                </div>
                            </div>

                            {/* Payment Form Container (Bootstrap Collapse) */}
                            <div className="collapse mt-4" id="paymentForm">
                                <div className="card shadow-sm">
                                    <div className="card-header bg-white py-3">
                                        <h5 className="mb-0">Información de Tarjeta</h5>
                                    </div>
                                    <div className="card-body">
                                        <form id="checkoutForm" onSubmit={handleCheckoutSubmit} noValidate>
                                            <div className="mb-3">
                                                <label htmlFor="cardName" className="form-label">Nombre en la tarjeta</label>
                                                <input type="text" className="form-control" id="cardName" name="cardName" 
                                                    value={checkoutForm.cardName} onChange={handleCheckoutChange} required />
                                                <div className="invalid-feedback">Introduce el nombre del titular.</div>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="billingEmail" className="form-label">Email para factura</label>
                                                <input type="email" className="form-control" id="billingEmail" name="billingEmail" 
                                                    value={checkoutForm.billingEmail} onChange={handleCheckoutChange} required />
                                                <div className="invalid-feedback">Introduce un email válido.</div>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="cardNumber" className="form-label">Número de tarjeta</label>
                                                <input type="text" className="form-control" id="cardNumber" name="cardNumber" 
                                                       placeholder="0000 0000 0000 0000" maxLength={19} 
                                                       value={checkoutForm.cardNumber} onChange={handleCheckoutChange} required />
                                                <div className="invalid-feedback">El número de tarjeta debe tener 16 dígitos.</div>
                                            </div>
                                            <div className="row">
                                                <div className="col-6 mb-3">
                                                    <label htmlFor="cardExpiry" className="form-label">Caducidad (MM/YY)</label>
                                                    <input type="text" className="form-control" id="cardExpiry" name="cardExpiry" 
                                                           placeholder="MM/YY" maxLength={5} 
                                                           value={checkoutForm.cardExpiry} onChange={handleCheckoutChange} required />
                                                    <div className="invalid-feedback">Introduce una fecha válida (MM/YY) no expirada.</div>
                                                </div>
                                                <div className="col-6 mb-3">
                                                    <label htmlFor="cardCvv" className="form-label">CVV</label>
                                                    <input type="password" className="form-control" id="cardCvv" name="cardCvv"
                                                           maxLength={3} autoComplete="off" 
                                                           value={checkoutForm.cardCvv} onChange={handleCheckoutChange} required />
                                                    <div className="invalid-feedback">El CVV debe tener 3 dígitos.</div>
                                                </div>
                                            </div>
                                            <div className="d-grid gap-2">
                                                <button type="submit" className="btn btn-success">Confirmar Pago</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </main>
    );
}
