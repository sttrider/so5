import * as React from "react";
import {useEffect, useState} from "react";
import axios from "axios";

const CategoryInput = ({onChange, onBlur, name}, ref) => {

    const [category, setCategory] = useState([]);

    useEffect(() => {
        const getCategory = async () => {
            const response = await axios.get('http://localhost:8080/category/')
            console.log(response.data);
            setCategory(response.data);
        }
        getCategory();
    }, []);

    console.log("CategoryInput")

    return (<select name={name} ref={ref} onChange={onChange} onBlur={onBlur}>
        <option>Choose one</option>
        {category && category.map(({id, name}) => (
            <option key={id} value={id}>{name}</option>
        ))}
    </select>);
}

export default React.memo(React.forwardRef(CategoryInput));