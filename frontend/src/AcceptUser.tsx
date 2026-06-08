import { useState } from 'react';

export default function AcceptUser() {
    const [userId, setUserId] = useState('');
    const [role, setRole] = useState('GROUP_USER');

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        const token = localStorage.getItem('accessToken');

        const res = await fetch(`/api/users/${userId}/accept`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ role })
        });
        alert(res.ok ? 'User accepted!' : 'Failed to accept user');
    };

    return (
        <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', width: '250px', gap: '10px' }}>
            <h2>Accept User</h2>
            <input placeholder="User UUID" required onChange={e => setUserId(e.target.value)} />
            <select value={role} onChange={e => setRole(e.target.value)}>
                <option value="GROUP_USER">Group User</option>
                <option value="TASK_MASTER">Task Master</option>
                <option value="OWNER">Owner</option>
            </select>
            <button type="submit">Accept</button>
        </form>
    );
}