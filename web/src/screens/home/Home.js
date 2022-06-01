import * as React from "react";
import {useEffect, useState, createContext, useCallback} from "react";
import axios from "axios";
import CategoryInput from "../../components/category/CategoryInput";
import Cart from "../../components/cart/Cart";
import Login from "../../components/login/Login";
import ProductList from "../../components/product/ProductList";
import {Col, Container, Row} from "react-bootstrap";

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
    return (
        <CartContext.Provider value={cart}>
            <UserContext.Provider value={user}>
                <Container as="section" className="py-5">
                    <Login setUser={setUser}/>
                </Container>
                <div className="py-5 bg-light">
                    <Container>
                        <Row>
                            <Col>
                                <CategoryInput onChange={handleOnChange}/>
                            </Col>
                        </Row>
                        <ProductList products={products} addCart={addCart}/>
                        <Cart clearCart={clearCart}/>
                    </Container>
                </div>
            </UserContext.Provider>
        </CartContext.Provider>
    );
});

export {CartContext, UserContext, Home};