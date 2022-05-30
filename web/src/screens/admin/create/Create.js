import * as React from "react";
import {Link, useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";

function Create() {

    const navigate = useNavigate();
    const {register, handleSubmit} = useForm({
        defaultValues: {
            enabled: true,
            image: "image"
        }
    });

    const onSubmit = async (data) => {
        const response = await axios.post('http://localhost:8080/product/', data, {
            headers: {
                Authorization: 'bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGdFAzU0dfQUxTbzBMVkFiZlUzMmFJem1lczUybXQ5bFVFX2cwcjA1LWlzIn0.eyJleHAiOjE2NTM5MjIxMjAsImlhdCI6MTY1MzkyMDMyMCwianRpIjoiNGY3ODliN2ItNGU4Yy00M2NmLTkxMjYtMjFmNWU0MzkxY2VlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9zbzUiLCJzdWIiOiIxZDM1NmQwMy1jOWMzLTQ1YWYtYTAyNy0yODI4NjA1MWZiYmIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJzbzVfd2ViIiwic2Vzc2lvbl9zdGF0ZSI6ImFiOTljN2Q0LTkzNzYtNDEwMy1hNmZlLTkxOGRhNTkzNDc1MyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsic281X3dlYiI6eyJyb2xlcyI6WyJhZG1pbiJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImFiOTljN2Q0LTkzNzYtNDEwMy1hNmZlLTkxOGRhNTkzNDc1MyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW5Ac281LmNvbSIsImdpdmVuX25hbWUiOiJBZG1pbiIsImVtYWlsIjoiYWRtaW5Ac281LmNvbSJ9.DjiMQPQmCClwBEz7i2cH9livRl2mGzy2hQgx_NJIN5VZ0-waObCdXKejwU948pn0N0XtQixlEpu5_1bDeAK56cHmhot2WaUnFUBnps53hid3FTcC-Kz4G8NKEU2o7kAScP4UvflaiNGSQdV7Ae0sNeUGj38nywR5WsyaIqZMrWmryJghIV_k77zWQXQQL_x9gQS-vFXePaoQo6nJ-UiwQl6ZzoXBkfTg8WYo4TsmxvzKBqwn-QDma-y21j8w_2L0WsOYhQLni05TeImNk3pwgUk1_EpjaDKXIW8KSjcDcaUQjj48_w__y_yDfvYQxTagP2CdZolZTfS0q_dglOPe_g'
            }
        })
        navigate("/admin", {replace: true});
        console.log(response.data);
    }

    return (
        <>
            <main>
                <h2>Creating</h2>
            </main>
            <nav>
                <Link to="/admin">Back</Link>
            </nav>
            <form onSubmit={handleSubmit(onSubmit)}>
                <input {...register("sku")} placeholder="Sku"/>
                <input {...register("name")} placeholder="Name"/>
                <input {...register("description")} placeholder="Description"/>
                <input type="number" {...register("price")} placeholder="Price"/>
                <input type="number" {...register("inventory")} placeholder="Inventory"/>
                <input {...register("shipmentDeliveryTimes")} placeholder="Shipment Delivery Times"/>
                <input type="checkbox" {...register("enabled")} placeholder="Enabled"/>
                <CategoryInput {...register("categoryId")} />
                <input type="hidden" {...register("image")} placeholder="Description"/>
                <input type="submit"/>
            </form>
        </>
    );
}

export default Create;