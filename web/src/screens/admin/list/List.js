import * as React from "react";
import {Link} from "react-router-dom";
import {useEffect, useState, useCallback} from "react";
import axios from "axios";
import CategoryInput from "../../../components/category/CategoryInput";
import ProductList from "../../../components/product/ProductList";

function List() {

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [category, setCategory] = useState(1);

    useEffect(() => {
        const getData = async () => {
            try {
                const response = await axios.post('http://localhost:8080/product/search', {categoryId: category})
                setProducts(response.data);
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
                <h2>Products list</h2>
            </main>
            <nav>
                <Link to="create">Create</Link>
                <Link to="/">Logout</Link>
            </nav>
            {loading && <div>A moment please...</div>}
            <CategoryInput onChange={handleOnChange} />
            <ProductList products={products}/>
        </>
    );
}

export default List;