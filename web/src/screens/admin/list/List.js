import * as React from "react";
import {Link} from "react-router-dom";
import {useEffect, useState, useCallback} from "react";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";
import ProductList from "../../../components/product/ProductList";
import {Col, Container, Row} from "react-bootstrap";

function List() {

    const [products, setProducts] = useState([]);
    const [category, setCategory] = useState(1);

    useEffect(() => {
        const getData = async () => {
            const response = await axios.post('http://localhost:8080/product/search', {categoryId: category})
            setProducts(response.data);
        }
        getData();
    }, [category]);

    const handleOnChange = useCallback((data) => setCategory(data.target.value), []);

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
                            <ProductList products={products}/>
                        </Col>
                    </Row>
                </Container>
            </div>
        </>
    );
}

export default List;