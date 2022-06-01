import * as React from "react";
import {Button, Card, CardImg, Col, Row} from "react-bootstrap";

export default function ProductList({products, addCart}) {

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
                                        {addCart &&
                                            <Button variant="outline-primary" onClick={() => addCart(product)}>Add to cart</Button>}
                                    </div>
                                    <small className="text-muted">Price: {product.price}</small>
                                </div>
                            </Card.Body>
                            <Card.Footer className="text-muted">Estimated delivery: {product.shipmentDeliveryTimes}</Card.Footer>
                        </Card>
                    </Col>
                ))}
        </Row>);
}