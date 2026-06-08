import { useState } from 'react';

export default function CreateGroup() {
    const [name, setName] = useState('');
    const [members, setMembers] = useState<string[]>([]);
    const [roles, setRoles] = useState<string[]>([]);

    const [tempMemberId, setTempMemberId] = useState('');
    const [tempRoles, setTempRoles] = useState<string[]>([]);

    const togglePermission = (perm: string) => {
        if (tempRoles.includes(perm)) {
            setTempRoles(tempRoles.filter(r => r !== perm));
        } else {
            setTempRoles([...tempRoles, perm]);
        }
    };

    const addMember = () => {
        if (!tempMemberId) return;
        if (tempRoles.length === 0) {
            alert('Please select at least one permission.');
            return;
        }

        const newMembers = tempRoles.map(() => tempMemberId);

        setMembers([...members, ...newMembers]);
        setRoles([...roles, ...tempRoles]);

        setTempMemberId('');
        setTempRoles([]);
    };

    const submit = async (e: React.FormEvent) => {
        e.preventDefault();
        const token = localStorage.getItem('accessToken');

        const payload: any = { name };

        if (members.length > 0) {
            payload.members = members;
            payload.roles = roles;
        }

        const res = await fetch('/api/groups/create', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });
        alert(res.ok ? 'Group created!' : 'Failed to create group');
    };

    return (
        <div style={{ display: 'flex', gap: '20px' }}>
            <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', width: '250px', gap: '10px' }}>
                <h2>Create Group</h2>
                <input placeholder="Group Name" required value={name} onChange={e => setName(e.target.value)} />
                <button type="submit">Create Group</button>
            </form>

            <div style={{ display: 'flex', flexDirection: 'column', width: '250px', gap: '10px', padding: '10px', border: '1px solid #ccc' }}>
                <h3>Add Members (Optional)</h3>
                <input placeholder="Member UUID" value={tempMemberId} onChange={e => setTempMemberId(e.target.value)} />

                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                    <strong>Permissions:</strong>

                    <label style={{ fontSize: '14px', cursor: 'pointer' }}>
                        <input
                            type="checkbox"
                            checked={tempRoles.includes('ADD_TASK')}
                            onChange={() => togglePermission('ADD_TASK')}
                        />
                        ADD_TASK
                    </label>

                    <label style={{ fontSize: '14px', cursor: 'pointer' }}>
                        <input
                            type="checkbox"
                            checked={tempRoles.includes('REMOVE_TASK')}
                            onChange={() => togglePermission('REMOVE_TASK')}
                        />
                        REMOVE_TASK
                    </label>

                    <label style={{ fontSize: '14px', cursor: 'pointer' }}>
                        <input
                            type="checkbox"
                            checked={tempRoles.includes('EDIT_TASK')}
                            onChange={() => togglePermission('EDIT_TASK')}
                        />
                        EDIT_TASK
                    </label>

                    <label style={{ fontSize: '14px', cursor: 'pointer' }}>
                        <input
                            type="checkbox"
                            checked={tempRoles.includes('ADD_USER_TO_GROUP')}
                            onChange={() => togglePermission('ADD_USER_TO_GROUP')}
                        />
                        ADD_USER_TO_GROUP
                    </label>

                    <label style={{ fontSize: '14px', cursor: 'pointer' }}>
                        <input
                            type="checkbox"
                            checked={tempRoles.includes('REMOVE_USER_FROM_GROUP')}
                            onChange={() => togglePermission('REMOVE_USER_FROM_GROUP')}
                        />
                        REMOVE_USER_FROM_GROUP
                    </label>
                </div>

                <button type="button" onClick={addMember}>Add to List</button>

                <ul style={{ fontSize: '14px', paddingLeft: '20px' }}>
                    {members.map((m, i) => (
                        <li key={i} style={{ wordBreak: 'break-all' }}>
                            {m.substring(0, 8)}... - <strong>{roles[i]}</strong>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}