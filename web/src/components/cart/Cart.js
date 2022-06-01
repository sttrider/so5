import * as React from "react";
import {useContext, useState} from "react";
import {CartContext, UserContext} from "../../screens/home/Home";
import axios from "axios";
import {Alert, Button} from "react-bootstrap";

export default function Cart({clearCart, removeFromCart}) {

    const [error, setError] = useState(null);

    const cart = useContext(CartContext);
    const user = useContext(UserContext);

    const handleClick = async () => {
        const newCart = cart.map(data => data.sku);
        try {
            await axios.post('http://localhost:8080/product/purchase/', newCart, {
                headers: {
                    Authorization: `bearer ${user.access_token}`
                }
            });
            setError(null);
            clearCart();
        } catch (err) {
            console.log(err);
            setError("You can't do the one click buy.");
            setTimeout(() => setError(null),  30000);
        }
    }

    const handleRemove = (product) => {
        removeFromCart(product);
    }

    let total = cart.reduce((sum, product) => sum + product.price, 0);

    return (
        <>
            <h4 className="d-flex justify-content-between align-items-center mb-3">
                <span className="text-primary">Your cart</span>
                <span className="badge bg-primary rounded-pill">{cart.length}</span>
            </h4>
            <ul className="list-group mb-3">
                {cart.length > 0 && cart.map((product) => (
                    <li key={product.sku} className="list-group-item d-flex justify-content-between lh-sm">
                        <div>
                            <h6 className="my-0">{product.name}</h6>
                            <small className="text-muted">{product.description}</small>
                        </div>
                        <div className="text-right">
                            <p className="my-0 text-muted">${product.price}</p>
                            <small className="text-muted">
                                <Button variant="link" className="btn-ultra-sm"
                                        onClick={() => handleRemove(product)}>Remove</Button>
                            </small>
                        </div>
                    </li>
                ))}
                <li className="list-group-item d-flex justify-content-between">
                    <span>Total (USD)</span>
                    <strong>${total}</strong>
                </li>
            </ul>
            {user &&
                <form className="card p-2">
                    <div className="input-group">
                        <Button variant="primary" className="btn-block" onClick={handleClick}>1-Click buy</Button>
                    </div>
                </form>
            }
            {error && <Alert variant="danger" className="mt-2">{error}</Alert>}
        </>);
}