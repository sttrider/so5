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
        const found = cart.filter((p) => p.sku === product.sku);
        if (found.length === 0) {
            cart.push(product);
            setCart([...new Set(cart)]);
        }
    }

    const clearCart = () => {
        setCart([]);
    }

    const removeFromCart = (product) => {
        const newCart = cart.filter((p) => p.sku !== product.sku);
        setCart([...new Set(newCart)]);
    }

    const handleOnChange = useCallback((data) => setCategory(data.target.value), []);
    return (
        <CartContext.Provider value={cart}>
            <UserContext.Provider value={user}>
                <Container as="section" className="py-3">
                    <Login setUser={setUser}/>
                </Container>
                <div className="py-5 bg-light">
                    <Container>
                        <Row className="g-5">
                            <Col md="5" lg="4" className="order-md-last">
                                <Cart clearCart={clearCart} removeFromCart={removeFromCart}/>
                            </Col>
                            <Col md="7" lg="8">
                                <Row>
                                    <Col>
                                        <CategoryInput onChange={handleOnChange}/>
                                    </Col>
                                </Row>
                                <ProductList products={products} addCart={addCart}/>
                            </Col>
                        </Row>
                    </Container>
                </div>
            </UserContext.Provider>
        </CartContext.Provider>
    );
});

export {CartContext, UserContext, Home};