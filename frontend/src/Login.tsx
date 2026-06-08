import { useState } from 'react';

export default function Login() {
    const [form, setForm] = useState({
        emailAddress: '', password: ''
    });

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        const res = await fetch('/auth/user/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(form)
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            alert('Login successful!');
        } else {
            alert('Login failed. Check your credentials.');
        }
    };

    return (
        <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', width: '250px', gap: '10px' }}>
            <h2>Login</h2>
            <input
                placeholder="Email"
                type="email"
                required
                onChange={e => setForm({...form, emailAddress: e.target.value})}
            />
            <input
                placeholder="Password"
                type="password"
                required
                onChange={e => setForm({...form, password: e.target.value})}
            />
            <button type="submit">Login</button>
        </form>
    );
}