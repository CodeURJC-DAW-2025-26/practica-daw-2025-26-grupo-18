import { useState } from "react";
import { Link, redirect, useLoaderData, useNavigate } from "react-router";
import type { CartDTO, CheckoutRequestDTO } from "../dtos/CartDTO";
import { getCart, removeItemFromCart, checkout } from "../services/cartService";
import { loadGlobalDataIntoStore } from "../services/globalService";

type CartLoaderData = {
    initialOrder: CartDTO | null;
};

export async function clientLoader(): Promise<CartLoaderData> {
    try {
        const cartData = await getCart();
        return { initialOrder: cartData };
    } catch (error: any) {
        if (error?.message?.includes("401")) {
            throw redirect("/new/login");
        }
        return { initialOrder: null };
    }
}

clientLoader.hydrate = true;

export default function Cart() {
    const { initialOrder } = useLoaderData<typeof clientLoader>();
    const [order, setOrder] = useState<CartDTO | null>(initialOrder);
    const [errorNoSeats, setErrorNoSeats] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    
    // Checkout form state
    const [checkoutForm, setCheckoutForm] = useState<CheckoutRequestDTO>({
        cardName: "",
        billingEmail: "",
        cardNumber: "",
        cardExpiry: "",
        cardCvv: ""
    });

    const navigate = useNavigate();

    const handleRemoveItem = async (itemId: number) => {
        try {
            const updatedOrder = await removeItemFromCart(itemId);
            setOrder(updatedOrder);
        } catch (error) {
            setErrorMessage("Error al eliminar el producto del carrito.");
        }
    };

    // Validation helpers
    const validateCardName = (v: string) => v.trim().length >= 3;
    const validateEmail = (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
    const validateCardNumber = (v: string) => v.replace(/\D/g, '').length === 16;
    const validateExpiry = (v: string) => {
        if (!/^\d{2}\/\d{2}$/.test(v)) return false;
        const [mm, yy] = v.split('/').map(Number);
        if (mm < 1 || mm > 12) return false;
        const now = new Date();
        const expDate = new Date(2000 + yy, mm - 1, 1);
        return expDate > now;
    };
    const validateCvv = (v: string) => /^\d{3}$/.test(v);

    const formErrors = {
        cardName: !validateCardName(checkoutForm.cardName),
        billingEmail: !validateEmail(checkoutForm.billingEmail),
        cardNumber: !validateCardNumber(checkoutForm.cardNumber),
        cardExpiry: !validateExpiry(checkoutForm.cardExpiry),
        cardCvv: !validateCvv(checkoutForm.cardCvv),
    };

    // Only show validation feedback if user has started typing in that field
    const [touched, setTouched] = useState<Record<string, boolean>>({});

    const handleCheckoutChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        // Auto-format card number with spaces every 4 digits
        if (name === 'cardNumber') {
            const digits = value.replace(/\D/g, '').slice(0, 16);
            const formatted = digits.replace(/(\d{4})(?=\d)/g, '$1 ');
            setCheckoutForm(prev => ({ ...prev, cardNumber: formatted }));
        } else if (name === 'cardExpiry') {
            // Auto-format expiry as MM/YY
            const digits = value.replace(/\D/g, '').slice(0, 4);
            const formatted = digits.length > 2 ? digits.slice(0, 2) + '/' + digits.slice(2) : digits;
            setCheckoutForm(prev => ({ ...prev, cardExpiry: formatted }));
        } else {
            setCheckoutForm(prev => ({ ...prev, [name]: value }));
        }
        setTouched(prev => ({ ...prev, [name]: true }));
    };

    const isFormValid = Object.values(formErrors).every(e => !e);

    const handleCheckoutSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorNoSeats(false);
        setErrorMessage("");

        try {
            const payload = {
                ...checkoutForm,
                cardNumber: checkoutForm.cardNumber.replace(/\D/g, ''), // Strip spaces before sending
            };
            await checkout(payload);
            // Refresh global data to update user roles (e.g. if they just bought a subscription)
            await loadGlobalDataIntoStore(true);
            navigate("/new"); // Redirect to home or success page after successful checkout
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
                        <Link to="/new" className="btn btn-secondary d-inline-flex align-items-center gap-2" style={{ backgroundColor: "#85613d", borderColor: "#85613d" }}>
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
                                            onClick={() => setShowPaymentForm(prev => !prev)}
                                            disabled={!order || !order.items || order.items.length === 0}>
                                            {showPaymentForm ? "Cancelar" : "Pagar Ahora"}
                                        </button>
                                    </div>
                                </div>
                            </div>

                            {/* Payment Form */}
                            {showPaymentForm && <div className="mt-4">
                                <div className="card shadow-sm">
                                    <div className="card-header bg-white py-3">
                                        <h5 className="mb-0">Información de Tarjeta</h5>
                                    </div>
                                    <div className="card-body">
                                        <form id="checkoutForm" onSubmit={handleCheckoutSubmit} noValidate>
                                            <div className="mb-3">
                                                <label htmlFor="cardName" className="form-label">Nombre en la tarjeta</label>
                                                <input type="text"
                                                    className={`form-control ${touched.cardName ? (formErrors.cardName ? 'is-invalid' : 'is-valid') : ''}`}
                                                    id="cardName" name="cardName"
                                                    value={checkoutForm.cardName} onChange={handleCheckoutChange} />
                                                <div className="invalid-feedback">Introduce el nombre del titular (mín. 3 caracteres).</div>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="billingEmail" className="form-label">Email para factura</label>
                                                <input type="email"
                                                    className={`form-control ${touched.billingEmail ? (formErrors.billingEmail ? 'is-invalid' : 'is-valid') : ''}`}
                                                    id="billingEmail" name="billingEmail"
                                                    value={checkoutForm.billingEmail} onChange={handleCheckoutChange} />
                                                <div className="invalid-feedback">Introduce un email válido.</div>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="cardNumber" className="form-label">Número de tarjeta</label>
                                                <input type="text"
                                                    className={`form-control ${touched.cardNumber ? (formErrors.cardNumber ? 'is-invalid' : 'is-valid') : ''}`}
                                                    id="cardNumber" name="cardNumber"
                                                    placeholder="0000 0000 0000 0000" maxLength={19}
                                                    value={checkoutForm.cardNumber} onChange={handleCheckoutChange} />
                                                <div className="invalid-feedback">El número de tarjeta debe tener 16 dígitos.</div>
                                            </div>
                                            <div className="row">
                                                <div className="col-6 mb-3">
                                                    <label htmlFor="cardExpiry" className="form-label">Caducidad (MM/YY)</label>
                                                    <input type="text"
                                                        className={`form-control ${touched.cardExpiry ? (formErrors.cardExpiry ? 'is-invalid' : 'is-valid') : ''}`}
                                                        id="cardExpiry" name="cardExpiry"
                                                        placeholder="MM/YY" maxLength={5}
                                                        value={checkoutForm.cardExpiry} onChange={handleCheckoutChange} />
                                                    <div className="invalid-feedback">Introduce una fecha válida (MM/YY) no expirada.</div>
                                                </div>
                                                <div className="col-6 mb-3">
                                                    <label htmlFor="cardCvv" className="form-label">CVV</label>
                                                    <input type="password"
                                                        className={`form-control ${touched.cardCvv ? (formErrors.cardCvv ? 'is-invalid' : 'is-valid') : ''}`}
                                                        id="cardCvv" name="cardCvv"
                                                        maxLength={3} autoComplete="off"
                                                        value={checkoutForm.cardCvv} onChange={handleCheckoutChange} />
                                                    <div className="invalid-feedback">El CVV debe tener 3 dígitos.</div>
                                                </div>
                                            </div>
                                            <div className="d-grid gap-2">
                                                <button type="submit" className="btn btn-success" disabled={!isFormValid}>Confirmar Pago</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>}
                        </div>
                    </div>
                </div>
            </section>
        </main>
    );
}
