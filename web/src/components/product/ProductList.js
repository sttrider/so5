import * as React from "react";
import {Button, Card, CardImg, Col, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

export default function ProductList({products, addCart, changeStateProduct, deleteProduct}) {
    const navigate = useNavigate();

    const handleEdit = ({sku}) => {
        navigate(`/admin/${sku}`, {replace: true});
    }

    return (
        <Row sm="2" md="3" className="g-3 py-3">
            {products &&
                products.map((product) => (
                    <Col key={product.sku}>
                        <Card>
                            {product.image && <CardImg src={product.image} variant="top" height="225" width="100%"/>}
                            <Card.Body>
                                <Card.Title>{product.name}</Card.Title>
                                <Card.Text>{product.description}</Card.Text>
                                <div className="d-flex justify-content-between align-items-center">
                                    <div className="btn-group">
                                        {changeStateProduct && <>
                                            <Button variant="outline-secondary" size="sm" onClick={() => handleEdit(product)}>Edit</Button>
                                            <Button variant={`outline-${product.enabled ? 'danger' : 'success'}`} size="sm" onClick={() => changeStateProduct(product)}>{product.enabled ? 'Inactivate' : 'Activate'}</Button>
                                            <Button variant="danger" size="sm" data-testid="removeFromCart" onClick={() => deleteProduct(product)}>Delete</Button>
                                        </>
                                        }
                                        {addCart &&
                                            <Button variant="outline-primary" data-testid="addToCart" onClick={() => addCart(product)}>Add to
                                                cart</Button>}
                                    </div>
                                    <small className="text-muted">Price: ${product.price}</small>
                                </div>
                            </Card.Body>
                            <Card.Footer className="text-muted">Estimated
                                delivery: {product.shipmentDeliveryTimes}</Card.Footer>
                        </Card>
                    </Col>
                ))}
        </Row>);
}