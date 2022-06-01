import * as React from "react";
import {Link, useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";
import login from "../../../service/loginService";
import {Button, Col, Container, Form, FormCheck, FormControl, FormGroup, Row} from "react-bootstrap";

function Create() {

    const navigate = useNavigate();
    const {register, handleSubmit} = useForm({
        defaultValues: {
            enabled: true
        }
    });

    const onSubmit = async (data) => {
        const newData = {...data};
        if (data.image.length === 1) {
            newData.image = await convertToBase64(data.image[0]);
        } else {
            newData.image = null;
        }
        const user = await login({username: "admin@so5.com", password: "admin"});
        try {
            await axios.post('http://localhost:8080/product/', newData, {
                headers: {
                    Authorization: `bearer ${user.access_token}`
                }
            })
            navigate("/admin", {replace: true});
        } catch (err) {
            console.log(err);
        }
    }

    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const fileReader = new FileReader();
            fileReader.readAsDataURL(file);
            fileReader.onload = () => {
                resolve(fileReader.result);
            };
            fileReader.onerror = (error) => {
                reject(error);
            };
        });
    };

    return (
        <>
            <Container className="py-5">
                <nav>
                    <Link to="/admin">Back</Link>
                </nav>
                <Form onSubmit={handleSubmit(onSubmit)}>
                    <Row className="g-3">
                        <FormGroup className="col-sm-6" controlId="sku">
                            <Form.Label>Sku</Form.Label>
                            <FormControl {...register("sku")} placeholder="Sku"/>
                        </FormGroup>
                        <FormGroup className="col-sm-6" controlId="name">
                            <Form.Label>Name</Form.Label>
                            <FormControl {...register("name")} placeholder="Name"/>
                        </FormGroup>
                        <FormGroup className="col-sm-12" controlId="description">
                            <Form.Label>Description</Form.Label>
                            <FormControl as="textarea" {...register("description")} placeholder="Description"/>
                        </FormGroup>
                        <FormGroup className="col-sm-4" controlId="price">
                            <Form.Label>Price</Form.Label>
                            <FormControl type="number" {...register("price")} placeholder="Price"/>
                        </FormGroup>
                        <FormGroup className="col-sm-4" controlId="inventory">
                            <Form.Label>Inventory</Form.Label>
                            <FormControl type="number" {...register("inventory")} placeholder="Inventory"/>
                        </FormGroup>
                        <FormGroup className="col-sm-4" controlId="shipmentDeliveryTimes">
                            <Form.Label>Shipment Delivery Times</Form.Label>
                            <FormControl {...register("shipmentDeliveryTimes")} placeholder="Shipment Delivery Times"/>
                        </FormGroup>
                        <FormGroup className="col-sm-4" controlId="enabled">
                            <Form.Label>Enabled</Form.Label>
                            <FormCheck type="checkbox" {...register("enabled")} placeholder="Enabled"/>
                        </FormGroup>
                        <FormGroup className="col-sm-4" controlId="categoryId">
                            <Form.Label>Category</Form.Label>
                            <CategoryInput {...register("categoryId")} />
                        </FormGroup>
                        <FormGroup className="col-sm-4" controlId="image">
                            <Form.Label>Image</Form.Label>
                            <FormControl type="file" {...register("image")} placeholder="Image"/>
                        </FormGroup>
                        <Button type="submit">Create</Button>
                    </Row>
                </Form>
            </Container>
        </>
    );
}

export default Create;