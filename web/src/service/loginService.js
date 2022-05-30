import axios from "axios";

export default async function login(data) {

    const params = new URLSearchParams();
    params.append('grant_type', 'password');
    params.append('username', data.username);
    params.append('password', data.password);
    params.append('client_id', 'so5_web');
    const response = await axios.post('http://localhost:8180/realms/so5/protocol/openid-connect/token', params);
    return response.data;
}