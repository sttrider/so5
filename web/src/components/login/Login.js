import * as React from "react";
import {useContext} from "react";
import {useForm} from "react-hook-form";
import {UserContext} from "../../screens/home/Home";
import login from "../../service/loginService";

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
        return (<form onSubmit={handleSubmit(onSubmit)}>
            <input {...register("username")} placeholder="E-mail"/>
            <input type="password" {...register("password")} placeholder="Password"/>
            <input type="submit"/>
        </form>)
    }

    return (
        <button onClick={onLogout}>Logout</button>
    );
}