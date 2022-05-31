import * as React from "react";
import {Link, useNavigate} from "react-router-dom";
import {useForm} from "react-hook-form";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";

function Create() {

    const navigate = useNavigate();
    const {register, handleSubmit} = useForm({
        defaultValues: {
            enabled: true
        }
    });

    const onSubmit = async (data) => {
        console.log(data);
        const newData = {...data}
        newData.image = await convertToBase64(data.image[0])
        console.log(newData);
        const response = await axios.post('http://localhost:8080/product/', newData, {
            headers: {
                Authorization: 'bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGdFAzU0dfQUxTbzBMVkFiZlUzMmFJem1lczUybXQ5bFVFX2cwcjA1LWlzIn0.eyJleHAiOjE2NTM5NDk2MDMsImlhdCI6MTY1Mzk0NzgwMywianRpIjoiMzcxYTljYWYtNWY1My00ZmUwLTk5ZmQtMjlmNzE2YjllZjdlIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9zbzUiLCJzdWIiOiIxZDM1NmQwMy1jOWMzLTQ1YWYtYTAyNy0yODI4NjA1MWZiYmIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJzbzVfd2ViIiwic2Vzc2lvbl9zdGF0ZSI6Ijk4NzZhYzJhLTUyNjctNGY0NC1iY2FiLTBiNjQzZTA4MDRkOSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsic281X3dlYiI6eyJyb2xlcyI6WyJhZG1pbiJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6Ijk4NzZhYzJhLTUyNjctNGY0NC1iY2FiLTBiNjQzZTA4MDRkOSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IkFkbWluIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW5Ac281LmNvbSIsImdpdmVuX25hbWUiOiJBZG1pbiIsImVtYWlsIjoiYWRtaW5Ac281LmNvbSJ9.M9R6vBvOcjheQxSLrxNFd41r9LeVAzFbX7AATiHTbFCF_HncjkPGgZgwfbFMwDoEF-h--ytG0HLj5LJsVJYd4dQy43FEi_-3lbNCHOsNp93l65ltl3MQ-4trXMXEIfQg13qcyLeSFFyV04pKEHvzkgSSwusMPRHt2syhfPZwzTXg3x4tBQaAglFXQOMR5G01BckYo98IRSdhhbPUUDWMOZdPc5PHNfIQQ1TroguWRa9AZKncfRVexxufnoTa0Fg3L8plNMV2Eeki4pF-h4c-bB8VJVJYp169zX8i0dbdW6eIINHsu2_CJGXTGtgaS71l7EFpjplQcw3WTpMDQ4TNMA'
            }
        })
        navigate("/admin", {replace: true});
        console.log(response.data);
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
                <input type="file" {...register("image")} placeholder="Description" />
                <input type="submit"/>
            </form>
        </>
    );
}

export default Create;