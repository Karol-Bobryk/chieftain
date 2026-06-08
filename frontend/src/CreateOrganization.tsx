import { useState } from 'react';

export default function CreateOrganization() {
    const [form, setForm] = useState({
        name: '', surname: '', emailAddress: '', password: '', jobTitle: '', organizationName: ''
    });

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        const res = await fetch('/auth/user/create-with-organization', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(form)
        });
        alert(res.ok ? 'Organization created!' : 'Error creating organization');
    };

    return (
        <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', width: '250px', gap: '10px' }}>
            <h2>Create Organization</h2>
            <input placeholder="Name" required onChange={e => setForm({...form, name: e.target.value})} />
            <input placeholder="Surname" required onChange={e => setForm({...form, surname: e.target.value})} />
            <input placeholder="Email" type="email" required onChange={e => setForm({...form, emailAddress: e.target.value})} />
            <input placeholder="Password" type="password" required onChange={e => setForm({...form, password: e.target.value})} />
            <input placeholder="Job Title" required onChange={e => setForm({...form, jobTitle: e.target.value})} />
            <input placeholder="New Organization Name" required onChange={e => setForm({...form, organizationName: e.target.value})} />
            <button type="submit">Create</button>
        </form>
    );
}