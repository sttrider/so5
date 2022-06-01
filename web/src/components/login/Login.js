import * as React from "react";
import {useContext} from "react";
import {useForm} from "react-hook-form";
import {UserContext} from "../../screens/home/Home";
import login from "../../service/loginService";
import {Col, Form, FormControl, FormGroup, Row} from "react-bootstrap";

export default function Login({setUser}) {

    const user = useContext(UserContext);

    const {register, handleSubmit} = useForm();

    const onSubmit = async (data) => {
        const response = await login(data)
        setUser(response);

    }

    const onLogout = () => {
        setUser(null);
    }

    if (user == null) {
        return (<Row className="py-lg-1">
            <Col lg="4" md="8" className="mx-auto text-center">
                <Form onSubmit={handleSubmit(onSubmit)}>
                    <h1 className="h3 mb-3 fw-normal">Please sign in</h1>
                    <FormGroup className="form-floating">
                        <FormControl {...register("username")} placeholder="E-mail"/>
                        <Form.Label>E-mail</Form.Label>
                    </FormGroup>
                    <FormGroup className="form-floating mb-2">
                        <FormControl type="password" {...register("password")} placeholder="Password"/>
                        <Form.Label>Password</Form.Label>
                    </FormGroup>
                    <p>
                        <input type="submit" className="w-100 btn btn-lg btn-primary" value="Sign in"/>
                    </p>
                </Form>
            </Col>
        </Row>)
    }

    return (
        <button onClick={onLogout}>Logout</button>
    );
}