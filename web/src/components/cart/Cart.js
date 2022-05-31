import * as React from "react";
import {useContext} from "react";
import {CartContext, UserContext} from "../../screens/home/Home";
import axios from "axios";
import ProductList from "../product/ProductList";

export default function Cart({clearCart}) {

    const cart = useContext(CartContext);
    const user = useContext(UserContext);

    const handleClick = async () => {
        console.log(cart)
        const newCart = cart.map(data => data.sku);
        try {
            const response = await axios.post('http://localhost:8080/product/purchase/', newCart, {
                headers: {
                    Authorization: `bearer ${user.access_token}`
                }
            });
            console.log(response);
            clearCart();
        } catch (err) {
            console.log(err)
        }
    }

    return (
        <>
            {cart.length > 0 && (
                <>
                    <p>Cart</p>
                    <div>
                        {user && <button onClick={handleClick}>1-Click buy</button>}
                    </div>
                    <ProductList products={cart}/>
                </>
            )}
        </>);
}