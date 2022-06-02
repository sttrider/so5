import * as React from "react";
import {Link, useNavigate, useParams} from "react-router-dom";
import {useForm} from "react-hook-form";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";
import login from "../../../service/loginService";
import {Button, Col, Container, Form, FormCheck, FormControl, FormGroup, Row} from "react-bootstrap";
import {useEffect} from "react";

function Create() {

    const navigate = useNavigate();
    const {register, handleSubmit, setValue} = useForm({
        defaultValues: {
            enabled: true,
            categoryId: 1
        }
    });
    const {sku} = useParams();

    useEffect(() => {

        const getData = async () => {
            const {data} = await axios.get(`http://localhost:8080/product/${sku}`)
            for (let i in data) {
                if (data.hasOwnProperty(i)) {
                    setValue(i, data[i]);
                }
            }
            setValue("categoryId", data.category.id);
            register("categoryId").ref.forceUpdate();
        }
        if (sku) {
            getData();
        }
    }, [sku]);

    const onSubmit = async (data) => {
        const newData = {...data};
        if (data.image.length === 1) {
            newData.image = await convertToBase64(data.image[0]);
        } else {
            newData.image = null;
        }
        const user = await login({username: "admin@so5.com", password: "admin"});
        try {
            if (newData.id == null) {
                await axios.post('http://localhost:8080/product/', newData, {
                    headers: {
                        Authorization: `bearer ${user.access_token}`
                    }
                });
            } else {
                await axios.put(`http://localhost:8080/product/${newData.id}`, newData, {
                    headers: {
                        Authorization: `bearer ${user.access_token}`
                    }
                });
            }
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
            <Container className="py-3">
                <nav>
                    <Link to="/admin" className="btn btn-outline-secondary">Back</Link>
                </nav>
            </Container>
            <div className="py-5 bg-light">
                <Container>
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
                                <FormControl {...register("shipmentDeliveryTimes")}
                                             placeholder="Shipment Delivery Times"/>
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
                            <Col>
                                <Button type="submit">Save</Button>
                            </Col>
                        </Row>
                    </Form>
                </Container>
            </div>
        </>
    );
}

export default Create;