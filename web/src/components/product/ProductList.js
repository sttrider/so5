import * as React from "react";

export default function ProductList({products, addCart}) {

    return (<ul>
        {products &&
            products.map((product) => (
                <li key={product.sku}>
                    <div>{product.name}</div>
                    {product.image && <div><img src={product.image} alt="description"/></div>}
                    {addCart &&
                        <div>
                            <button onClick={() => addCart(product)}>Add to cart</button>
                        </div>
                    }
                </li>
            ))}
    </ul>);
}