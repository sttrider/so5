import { render, screen } from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom'
import App from './App';

test('renders App', () => {
  render(<App />, {wrapper: MemoryRouter})
  const linkElement = screen.getByText(/SO5/i);
  expect(linkElement).toBeInTheDocument();
});
