import * as React from "react";
import {useEffect, useState} from "react";
import axios from "axios";
import {FormSelect} from "react-bootstrap";

const CategoryInput = ({onChange, onBlur, name}, ref) => {

    const [category, setCategory] = useState([]);

    useEffect(() => {
        const getCategory = async () => {
            const response = await axios.get('http://localhost:8080/category/')
            setCategory(response.data);
        }
        getCategory();
    }, []);


    return (<FormSelect name={name} ref={ref} onChange={onChange} onBlur={onBlur}>
        <option>Choose one</option>
        {category && category.map(({id, name}) => (
            <option key={id} value={id}>{name}</option>
        ))}
    </FormSelect>);
}

export default React.memo(React.forwardRef(CategoryInput));