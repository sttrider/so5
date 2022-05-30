import * as React from "react";
import {Link} from "react-router-dom";
import {useEffect, useState, useCallback} from "react";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";

function List() {

    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [category, setCategory] = useState(1);

    useEffect(() => {
        const getData = async () => {
            try {
                const response = await axios.post('http://localhost:8080/product/search', {categoryId: category})
                setData(response.data);
            } finally {
                setLoading(false)
            }
        }
        getData();
    }, [category]);

    const handleOnChange = useCallback((data) => setCategory(data.target.value), []);

    console.log("bbb")
    return (
        <>
            <main>
                <h2>Products list!</h2>
            </main>
            <nav>
                <Link to="create">Create</Link>
                <Link to="/">Logout</Link>
            </nav>
            {loading && <div>A moment please...</div>}
            <CategoryInput onChange={handleOnChange} />
            <ul>
                {data &&
                    data.map(({sku, name}) => (
                        <li key={sku}>
                            <h3>{name}</h3>
                        </li>
                    ))}
            </ul>
        </>
    );
}

export default List;