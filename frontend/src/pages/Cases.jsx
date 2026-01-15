import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, Button, Modal, Form } from 'react-bootstrap';

function Cases() {
    const [cases, setCases] = useState([]);
    const [selectedCase, setSelectedCase] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchCases();
    }, []);

    const fetchCases = async () => {
        try {
            const res = await axios.get('http://localhost:8080/fraud-cases');
            setCases(res.data);
        } catch (e) {
            console.error('Failed to fetch cases:', e);
        } finally {
            setLoading(false);
        }
    };

    const openCaseDetail = async (caseId) => {
        try {
            const res = await axios.get(`http://localhost:8080/fraud-cases/${caseId}`);
            setSelectedCase(res.data);
            setShowModal(true);
        } catch (e) {
            console.error('Failed to fetch case details:', e);
        }
    };

    const updateStatus = async (caseId, newStatus) => {
        try {
            await axios.patch(`http://localhost:8080/fraud-cases/${caseId}/status`, {
                status: newStatus,
                actorName: 'Admin',
                note: `Status changed to ${newStatus}`
            });
            fetchCases();
            if (selectedCase) {
                openCaseDetail(caseId);
            }
        } catch (e) {
            console.error('Failed to update status:', e);
        }
    };

    const getStatusBadgeClass = (status) => {
        switch (status) {
            case 'OPEN': return 'success';
            case 'IN_PROGRESS': return 'warning';
            case 'CLOSED': return 'info';
            default: return 'info';
        }
    };

    const getPriorityBadgeClass = (priority) => {
        switch (priority) {
            case 'CRITICAL': return 'danger';
            case 'HIGH': return 'danger';
            case 'MEDIUM': return 'warning';
            case 'LOW': return 'success';
            default: return 'info';
        }
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="neon-spinner"></div>
                <span className="loading-text">Loading Cases...</span>
            </div>
        );
    }

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2>FRAUD CASES</h2>
                    <small className="text-muted">INVESTIGATION & CASE MANAGEMENT</small>
                </div>
                <Button variant="outline-secondary" onClick={fetchCases}>
                    Refresh
                </Button>
            </div>

            <div className="neon-card">
                <Table className="table-neon" responsive>
                    <thead>
                        <tr>
                            <th>CASE ID</th>
                            <th>KULLANICI</th>
                            <th>TİP</th>
                            <th>DURUM</th>
                            <th>ÖNCELİK</th>
                            <th>AÇILMA TARİHİ</th>
                            <th>İŞLEMLER</th>
                        </tr>
                    </thead>
                    <tbody>
                        {cases.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="text-center py-4 text-muted">
                                    Henüz fraud case bulunmuyor.
                                </td>
                            </tr>
                        ) : cases.map(c => (
                            <tr key={c.caseId}>
                                <td>
                                    <span className="fw-bold text-white">{c.caseId}</span>
                                </td>
                                <td>
                                    <span className="text-neon-blue">{c.userId}</span>
                                </td>
                                <td>
                                    <span className="badge-pro purple">{c.caseType || 'FRAUD'}</span>
                                </td>
                                <td>
                                    <span className={`badge-pro ${getStatusBadgeClass(c.status)}`}>
                                        {c.status}
                                    </span>
                                </td>
                                <td>
                                    <span className={`badge-pro ${getPriorityBadgeClass(c.priority)}`}>
                                        {c.priority}
                                    </span>
                                </td>
                                <td style={{ color: '#e2e8f0', fontSize: '0.85rem' }}>
                                    {c.openedAt ? new Date(c.openedAt).toLocaleString() : '-'}
                                </td>
                                <td>
                                    <Button
                                        size="sm"
                                        variant="outline-primary"
                                        onClick={() => openCaseDetail(c.caseId)}
                                    >
                                        Detay
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>

            {/* Case Detail Modal */}
            <Modal
                show={showModal}
                onHide={() => setShowModal(false)}
                size="lg"
                centered
                contentClassName="bg-dark"
            >
                {selectedCase && (
                    <div className="neon-card" style={{ margin: 0, border: 'none' }}>
                        <Modal.Header closeButton className="border-0">
                            <Modal.Title className="text-neon-blue font-orbitron">
                                CASE: {selectedCase.caseId}
                            </Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <div className="row mb-4">
                                <div className="col-md-6">
                                    <div className="mb-3">
                                        <label className="small text-muted d-block">Kullanıcı</label>
                                        <span className="text-white fw-bold">{selectedCase.userId}</span>
                                    </div>
                                    <div className="mb-3">
                                        <label className="small text-muted d-block">Case Tipi</label>
                                        <span className="badge-pro purple">{selectedCase.caseType || 'FRAUD'}</span>
                                    </div>
                                    <div className="mb-3">
                                        <label className="small text-muted d-block">Açan</label>
                                        <span className="text-white">{selectedCase.openedBy || 'System'}</span>
                                    </div>
                                </div>
                                <div className="col-md-6">
                                    <div className="mb-3">
                                        <label className="small text-muted d-block">Durum</label>
                                        <span className={`badge-pro ${getStatusBadgeClass(selectedCase.status)}`}>
                                            {selectedCase.status}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <label className="small text-muted d-block">Öncelik</label>
                                        <span className={`badge-pro ${getPriorityBadgeClass(selectedCase.priority)}`}>
                                            {selectedCase.priority}
                                        </span>
                                    </div>
                                    <div className="mb-3">
                                        <label className="small text-muted d-block">Açılma Tarihi</label>
                                        <span className="text-white">
                                            {selectedCase.openedAt ? new Date(selectedCase.openedAt).toLocaleString() : '-'}
                                        </span>
                                    </div>
                                </div>
                            </div>

                            {/* Status Update */}
                            <div className="mb-4">
                                <label className="small text-muted d-block mb-2">Durum Güncelle</label>
                                <div className="d-flex gap-2">
                                    <Button
                                        size="sm"
                                        variant={selectedCase.status === 'OPEN' ? 'success' : 'outline-success'}
                                        onClick={() => updateStatus(selectedCase.caseId, 'OPEN')}
                                    >
                                        OPEN
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant={selectedCase.status === 'IN_PROGRESS' ? 'warning' : 'outline-warning'}
                                        onClick={() => updateStatus(selectedCase.caseId, 'IN_PROGRESS')}
                                    >
                                        IN PROGRESS
                                    </Button>
                                    <Button
                                        size="sm"
                                        variant={selectedCase.status === 'CLOSED' ? 'secondary' : 'outline-secondary'}
                                        onClick={() => updateStatus(selectedCase.caseId, 'CLOSED')}
                                    >
                                        CLOSED
                                    </Button>
                                </div>
                            </div>

                            {/* Action History */}
                            <div>
                                <label className="small text-muted d-block mb-2">
                                    AKSİYON GEÇMİŞİ (AUDIT TRAIL)
                                </label>
                                <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
                                    {selectedCase.history && selectedCase.history.length > 0 ? (
                                        <Table className="table-neon" size="sm">
                                            <thead>
                                                <tr>
                                                    <th>Zaman</th>
                                                    <th>Aksiyon</th>
                                                    <th>Yapan</th>
                                                    <th>Not</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {selectedCase.history.map((h, idx) => (
                                                    <tr key={idx}>
                                                        <td style={{ color: '#e2e8f0', fontSize: '0.85rem' }}>
                                                            {new Date(h.timestamp).toLocaleString()}
                                                        </td>
                                                        <td>
                                                            <span className="badge-pro info">{h.actionType}</span>
                                                        </td>
                                                        <td className="text-white">{h.actor}</td>
                                                        <td className="small text-muted">{h.note || '-'}</td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </Table>
                                    ) : (
                                        <p className="text-muted small mb-0">Henüz aksiyon kaydı yok.</p>
                                    )}
                                </div>
                            </div>
                        </Modal.Body>
                        <Modal.Footer className="border-0">
                            <Button variant="outline-light" onClick={() => setShowModal(false)}>
                                Kapat
                            </Button>
                        </Modal.Footer>
                    </div>
                )}
            </Modal>
        </div>
    );
}

export default Cases;
