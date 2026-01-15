import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, Button, Badge, Form, Modal } from 'react-bootstrap';

function Rules() {
    const [rules, setRules] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        ruleId: '', condition: '', action: 'FORCE_2FA', priority: 1, active: true
    });
    const [isEdit, setIsEdit] = useState(false);

    useEffect(() => { fetchRules(); }, []);

    const fetchRules = async () => {
        const res = await axios.get('http://localhost:8080/risk-rules');
        setRules(res.data);
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Emin misiniz?')) return;
        await axios.delete(`http://localhost:8080/risk-rules/${id}`);
        fetchRules();
    };

    const handleSave = async () => {
        try {
            if (isEdit) {
                await axios.put(`http://localhost:8080/risk-rules/${formData.ruleId}`, formData);
            } else {
                await axios.post('http://localhost:8080/risk-rules', formData);
            }
            setShowModal(false);
            fetchRules();
        } catch (e) {
            alert('Hata: ' + e.message);
        }
    };

    const openEdit = (rule) => {
        setFormData(rule);
        setIsEdit(true);
        setShowModal(true);
    };

    const openNew = () => {
        setFormData({ ruleId: '', condition: '', action: 'FORCE_2FA', priority: 1, active: true });
        setIsEdit(false);
        setShowModal(true);
    };

    const toggleActive = async (id, currentStatus) => {
        const endpoint = currentStatus
            ? `http://localhost:8080/risk-rules/${id}/deactivate`
            : `http://localhost:8080/risk-rules/${id}/activate`;
        await axios.patch(endpoint);
        fetchRules();
    };

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2>RISK KURALLARI</h2>
                    <small className="text-muted">FRAUD DETECTION RULES ENGINE</small>
                </div>
                <Button variant="outline-primary" onClick={openNew}>+ YENİ KURAL</Button>
            </div>

            <div className="neon-card">
                <Table className="table-neon" responsive>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>KOŞUL (CONDITION)</th>
                            <th>AKSİYON</th>
                            <th>ÖNCELİK</th>
                            <th>DURUM</th>
                            <th>İŞLEMLER</th>
                        </tr>
                    </thead>
                    <tbody>
                        {rules.map(r => (
                            <tr key={r.ruleId}>
                                <td><span className="fw-bold text-white">{r.ruleId}</span></td>
                                <td style={{ fontFamily: 'monospace', color: '#00ff9d', fontSize: '0.9rem' }}>{r.condition}</td>
                                <td><span className="badge-pro info">{r.action}</span></td>
                                <td className="text-muted fw-bold">{r.priority}</td>
                                <td>
                                    <span
                                        className={`badge-pro ${r.active ? 'success' : 'danger'}`}
                                        onClick={() => toggleActive(r.ruleId, r.active)}
                                        style={{ cursor: 'pointer' }}
                                    >
                                        {r.active ? 'AKTIF' : 'PASIF'}
                                    </span>
                                </td>
                                <td>
                                    <Button size="sm" variant="outline-warning" className="me-2 rounded-circle" onClick={() => openEdit(r)}><i className="bi bi-pencil-fill"></i></Button>
                                    <Button size="sm" variant="outline-danger" className="rounded-circle" onClick={() => handleDelete(r.ruleId)}><i className="bi bi-trash-fill"></i></Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>

            <Modal show={showModal} onHide={() => setShowModal(false)} contentClassName="bg-dark text-white" centered>
                <div className="neon-card" style={{ margin: 0, border: 'none' }}>
                    <Modal.Header closeButton className="border-0">
                        <Modal.Title className="text-neon-blue font-orbitron">{isEdit ? 'KURAL DÜZENLE' : 'YENİ KURAL'}</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Form>
                            {!isEdit && (
                                <Form.Group className="mb-3">
                                    <Form.Label className="small text-muted">RULE ID</Form.Label>
                                    <Form.Control type="text" value={formData.ruleId} onChange={e => setFormData({ ...formData, ruleId: e.target.value })} placeholder="Otomatik veya RR-99" />
                                </Form.Group>
                            )}
                            <Form.Group className="mb-3">
                                <Form.Label className="small text-muted">CONDITION</Form.Label>
                                <Form.Control className="font-monospace text-neon-green" as="textarea" rows={3} value={formData.condition} onChange={e => setFormData({ ...formData, condition: e.target.value })} placeholder="service == 'BiP' && amount > 500" />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label className="small text-muted">ACTION</Form.Label>
                                <Form.Select value={formData.action} onChange={e => setFormData({ ...formData, action: e.target.value })}>
                                    <option value="FORCE_2FA">FORCE_2FA</option>
                                    <option value="PAYMENT_REVIEW">PAYMENT_REVIEW</option>
                                    <option value="TEMPORARY_BLOCK">TEMPORARY_BLOCK</option>
                                    <option value="OPEN_FRAUD_CASE">OPEN_FRAUD_CASE</option>
                                    <option value="ANOMALY_ALERT">ANOMALY_ALERT</option>
                                </Form.Select>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label className="small text-muted">PRIORITY (1 = Highest)</Form.Label>
                                <Form.Control type="number" value={formData.priority} onChange={e => setFormData({ ...formData, priority: parseInt(e.target.value) })} />
                            </Form.Group>
                            <Form.Check
                                type="switch"
                                label="AKTİF"
                                className="text-neon-blue"
                                checked={formData.active}
                                onChange={e => setFormData({ ...formData, active: e.target.checked })}
                            />
                        </Form>
                    </Modal.Body>
                    <Modal.Footer className="border-0">
                        <Button variant="outline-light" onClick={() => setShowModal(false)}>İPTAL</Button>
                        <Button variant="primary" onClick={handleSave}>KAYDET</Button>
                    </Modal.Footer>
                </div>
            </Modal>
        </div>
    );
}

export default Rules;
