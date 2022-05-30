import * as React from "react";
import {useEffect, useState, createContext, useCallback} from "react";
import axios from "axios";
import CategoryInput from "../../components/category/CategoryInput";
import Cart from "../../components/cart/Cart";
import Login from "../../components/login/Login";

const CartContext = createContext();
const UserContext = createContext();

const Home = React.memo(() => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [category, setCategory] = useState(1);
    const [cart, setCart] = useState([]);
    const [user, setUser] = useState(null);

    useEffect(() => {
        const getData = async () => {
            try {
                const response = await axios.post('http://localhost:8080/product/search', {categoryId: category})
                setProducts(response.data);
                console.log(response.data)
            } finally {
                setLoading(false)
            }
        }
        getData();
    }, [category]);

    const addCart = (product) => {
        cart.push(product);
        setCart([...new Set(cart)]);
    }

    const clearCart = () => {
        setCart([]);
    }

    const handleOnChange = useCallback((data) => setCategory(data.target.value), []);
    console.log("home")
    return (
        <CartContext.Provider value={cart}>
            <UserContext.Provider value={user}>
                <main>
                    <h2>Products list!</h2>
                </main>
                {loading && <div>A moment please...</div>}
                <Login setUser={setUser}/>
                <CategoryInput onChange={handleOnChange}/>
                <ul>
                    {products && products.map((product) => (
                        <li key={product.sku}>
                            <h3>{product.name}</h3>
                            <div>
                                <button onClick={() => addCart(product)}>Add to cart</button>
                            </div>
                        </li>
                    ))}
                </ul>
                <Cart clearCart={clearCart}/>
            </UserContext.Provider>
        </CartContext.Provider>
    );
});

export {CartContext, UserContext, Home};