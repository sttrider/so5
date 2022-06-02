import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event'
import {MemoryRouter} from 'react-router-dom'
import {Home} from "./Home";
import axios from 'axios';

jest.mock("axios");

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
const fakeCategory = {
    id: 1,
    name: "category"
};
const fakeUser = {
    access_token: "access_token"
}

describe('Home component', () => {
    beforeEach(() => {
        axios.get.mockResolvedValue({data: [fakeCategory]});
        axios.post.mockResolvedValue({data: [fakeProduct1, fakeProduct2]});
    });

    test('Should have empty cart', async () => {

        render(<Home/>, {wrapper: MemoryRouter})
        const badge = screen.getByTestId("badge")
        expect(badge).toHaveTextContent("0");
    });
    test('Rendering products', async () => {

        render(<Home/>, {wrapper: MemoryRouter})
        const linkElement = await screen.findByText(/name 1/i);
        const badge = screen.getByTestId("badge")
        expect(linkElement).toBeInTheDocument();
        expect(badge).toHaveTextContent("0");
    });
    test('Adding to cart', async () => {

        render(<Home/>, {wrapper: MemoryRouter})

        const badge = screen.getByTestId("badge");
        expect(badge).toHaveTextContent("0");

        const addToCart = await screen.findAllByTestId("addToCart");

        addToCart.forEach(async (e) => await userEvent.click(e))
        expect(badge).toHaveTextContent("2");

        const total = screen.getByTestId("cartTotal");
        expect(total).toHaveTextContent("14");
    });

    test('Removing from cart', async () => {

        render(<Home/>, {wrapper: MemoryRouter})

        const badge = screen.getByTestId("badge");
        expect(badge).toHaveTextContent("0");

        const addToCart = await screen.findAllByTestId("addToCart");

        addToCart.forEach(async (e) => await userEvent.click(e))
        expect(badge).toHaveTextContent("2");

        const total = screen.getByTestId("cartTotal");
        expect(total).toHaveTextContent("14");

        const removeFromCart = await screen.findAllByTestId("removeFromCart")
        await userEvent.click(removeFromCart[0]);
        expect(badge).toHaveTextContent("1");
        expect(total).toHaveTextContent("12");
    });

    test('One click buy', async () => {

        render(<Home/>, {wrapper: MemoryRouter})

        const badge = screen.getByTestId("badge");
        expect(badge).toHaveTextContent("0");

        const addToCart = await screen.findAllByTestId("addToCart");

        addToCart.forEach(async (e) => await userEvent.click(e))
        expect(badge).toHaveTextContent("2");

        const total = screen.getByTestId("cartTotal");
        expect(total).toHaveTextContent("14");

        axios.post.mockResolvedValue({data: [fakeUser]});
        const submitLogin = await screen.findByTestId("submitLogin")
        await act(async () => {
            await userEvent.click(submitLogin);
        })

        axios.post.mockResolvedValue({data: {}});
        const oneClickBuy = await screen.findByTestId("oneClickBuy")
        expect(oneClickBuy).toBeTruthy()
        await act( async () => {
            await userEvent.click(oneClickBuy);
        })
        expect(badge).toHaveTextContent("0");
        expect(total).toHaveTextContent("0");
    });

    test('One click buy with error', async () => {

        render(<Home/>, {wrapper: MemoryRouter})

        const badge = screen.getByTestId("badge");
        expect(badge).toHaveTextContent("0");

        const addToCart = await screen.findAllByTestId("addToCart");

        addToCart.forEach(async (e) => await userEvent.click(e))
        expect(badge).toHaveTextContent("2");

        const total = screen.getByTestId("cartTotal");
        expect(total).toHaveTextContent("14");

        axios.post.mockResolvedValue({data: [fakeUser]});
        const submitLogin = await screen.findByTestId("submitLogin")
        await act(async () => {
            await userEvent.click(submitLogin);
        })

        axios.post.mockImplementationOnce(() =>
            Promise.reject(new Error("Not found.")),
        );
        const oneClickBuy = await screen.findByTestId("oneClickBuy")
        expect(oneClickBuy).toBeTruthy()
        await act( async () => {
            await userEvent.click(oneClickBuy);
        })
        expect(badge).toHaveTextContent("2");
        expect(total).toHaveTextContent("14");
        const onClickBuyError = await screen.findByTestId("onClickBuyError")
        expect(onClickBuyError).toBeTruthy()
    });
});