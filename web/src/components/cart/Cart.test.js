import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event'
import Cart from "./Cart";
import {CartContext, UserContext} from "../../screens/home/Home";

const fakeProduct1 = {
    id: 1,
    sku: "sku1",
    name: "name 1",
    description: "description",
    price: 2,
    enabled: true
};
const fakeProduct2 = {
    id: 1,
    sku: "sku2",
    name: "name 2",
    description: "description 2",
    price: 12,
    enabled: true
};

const customCartContextRender = (ui, {providerProps, ...renderOptions}) => {
    return render(
        <CartContext.Provider {...providerProps}>{ui}</CartContext.Provider>,
        renderOptions,
    )
}

const customUserCartContextRender = (ui, {userProps, ...renderOptions}) => {
    return customCartContextRender(<UserContext.Provider {...userProps}>{ui}</UserContext.Provider>,
        renderOptions
    )
}

describe('Cart component', () => {
    test('Should have empty cart', async () => {

        const providerProps = {
            value: []
        }

        customCartContextRender(<Cart/>, {providerProps});
        const badge = screen.getByTestId("badge")
        expect(badge).toHaveTextContent("0");

        expect(screen.queryByTestId("oneClickBuy")).toBeNull()
    });

    test('Should have two products in the cart', async () => {

        const providerProps = {
            value: [fakeProduct1, fakeProduct2]
        }

        customCartContextRender(<Cart/>, {providerProps});
        const badge = screen.getByTestId("badge")
        expect(badge).toHaveTextContent("2");

        const total = screen.getByTestId("cartTotal");
        expect(total).toHaveTextContent("14");

        expect(screen.queryByTestId("oneClickBuy")).toBeNull()
    });

    test('Should have two products in the cart and show one click buy button', async () => {

        const providerProps = {
            value: [fakeProduct1, fakeProduct2]
        }

        const userProps = {
            value: {
                access_token: "aaa"
            }
        }

        customUserCartContextRender(<Cart/>, {providerProps, userProps});
        const badge = screen.getByTestId("badge")
        expect(badge).toHaveTextContent("2");

        const total = screen.getByTestId("cartTotal");
        expect(total).toHaveTextContent("14");

        expect(await screen.findByTestId("oneClickBuy")).toBeTruthy()
    });
});