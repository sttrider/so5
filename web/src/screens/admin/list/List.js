import * as React from "react";
import {Link} from "react-router-dom";
import {useEffect, useState, useCallback} from "react";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";
import ProductList from "../../../components/product/ProductList";
import {Col, Container, Row} from "react-bootstrap";
import login from "../../../service/loginService";

function List() {

    const [products, setProducts] = useState([]);
    const [category, setCategory] = useState(1);
    const [admin, setAdmin] = useState({});

    useEffect(() => {
        const getData = async () => {
            const userAdmin = await login({username: "admin@so5.com", password: "admin"});
            setAdmin(userAdmin);

            await getProducts(userAdmin);
        }
        getData();
    }, [category]);

    const getProducts = async (userAdmin) => {
        const response = await axios.post('http://localhost:8080/product/list', {categoryId: category}, {
            headers: {
                Authorization: `bearer ${userAdmin.access_token}`
            }
        })
        setProducts(response.data);
    }

    const handleOnChange = useCallback((data) => setCategory(data.target.value), []);

    const handleChangeState = async ({sku, enabled}) => {
        await axios.put(`http://localhost:8080/product/${sku}/${!enabled}`, {}, {
            headers: {
                Authorization: `bearer ${admin.access_token}`
            }
        })
        await getProducts(admin);
    }

    const handleDeleteProduct = async ({id}) => {
        await axios.delete(`http://localhost:8080/product/${id}`, {
            headers: {
                Authorization: `bearer ${admin.access_token}`
            }
        })
        await getProducts(admin);
    }

    return (
        <>
            <Container as="section" className="py-3">
                <Link to="create" className="btn btn-outline-primary me-1">Create</Link>
                <Link to="/" className="btn btn-outline-secondary">Logout</Link>
            </Container>
            <div className="py-5 bg-light">
                <Container>
                    <Row>
                        <Col>
                            <CategoryInput onChange={handleOnChange}/>
                            <ProductList products={products} changeStateProduct={handleChangeState}
                                         deleteProduct={handleDeleteProduct}/>
                        </Col>
                    </Row>
                </Container>
            </div>
        </>
    );
}

export default List;