import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, Button } from 'react-bootstrap';

function Decisions() {
    const [decisions, setDecisions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchDecisions();
    }, []);

    const fetchDecisions = async () => {
        try {
            const res = await axios.get('http://localhost:8080/decisions');
            // Handle both paginated and non-paginated responses
            const data = res.data.content || res.data;
            // Sort by timestamp descending
            const sorted = Array.isArray(data)
                ? data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
                : [];
            setDecisions(sorted);
        } catch (e) {
            console.error('Failed to fetch decisions:', e);
        } finally {
            setLoading(false);
        }
    };

    const getActionBadgeClass = (action) => {
        switch (action) {
            case 'FORCE_2FA': return 'info';
            case 'PAYMENT_REVIEW': return 'warning';
            case 'TEMPORARY_BLOCK': return 'danger';
            case 'OPEN_FRAUD_CASE': return 'danger';
            case 'ANOMALY_ALERT': return 'purple';
            default: return 'info';
        }
    };

    if (loading) {
        return (
            <div className="loading-container">
                <div className="neon-spinner"></div>
                <span className="loading-text">Loading Decisions...</span>
            </div>
        );
    }

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2>KARAR LOGLARI</h2>
                    <small className="text-muted">DECISION AUDIT TRAIL & ANALYTICS</small>
                </div>
                <Button variant="outline-secondary" onClick={fetchDecisions}>
                    Refresh
                </Button>
            </div>

            <div className="neon-card">
                <Table className="table-neon" responsive>
                    <thead>
                        <tr>
                            <th>KARAR ID</th>
                            <th>KULLANICI</th>
                            <th>TETİKLENEN KURALLAR</th>
                            <th>SEÇİLEN AKSİYON</th>
                            <th>BASTIRILMIŞ</th>
                            <th>ZAMAN</th>
                        </tr>
                    </thead>
                    <tbody>
                        {decisions.length === 0 ? (
                            <tr>
                                <td colSpan="6" className="text-center py-4 text-muted">
                                    Henüz karar kaydı bulunmuyor.
                                </td>
                            </tr>
                        ) : decisions.map(d => (
                            <tr key={d.decisionId}>
                                <td>
                                    <span className="fw-bold text-white font-monospace">
                                        {d.decisionId}
                                    </span>
                                </td>
                                <td>
                                    <span className="text-neon-blue fw-bold">{d.userId}</span>
                                </td>
                                <td>
                                    <div className="d-flex flex-wrap gap-1">
                                        {d.triggeredRules && d.triggeredRules.map((rule, idx) => (
                                            <span key={idx} className="badge-pro info" style={{ fontSize: '0.7rem' }}>
                                                {rule}
                                            </span>
                                        ))}
                                    </div>
                                </td>
                                <td>
                                    <span className={`badge-pro ${getActionBadgeClass(d.selectedAction)}`}>
                                        {d.selectedAction}
                                    </span>
                                </td>
                                <td>
                                    {d.suppressedActions && d.suppressedActions.length > 0 ? (
                                        <div className="d-flex flex-wrap gap-1">
                                            {d.suppressedActions.map((action, idx) => (
                                                <span
                                                    key={idx}
                                                    className="text-muted small font-monospace"
                                                    style={{ textDecoration: 'line-through', opacity: 0.6 }}
                                                >
                                                    {action}
                                                </span>
                                            ))}
                                        </div>
                                    ) : (
                                        <span className="text-muted opacity-50">-</span>
                                    )}
                                </td>
                                <td style={{ color: '#e2e8f0', fontSize: '0.85rem', whiteSpace: 'nowrap' }}>
                                    {d.timestamp ? new Date(d.timestamp).toLocaleString() : '-'}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>

            {/* Statistics Summary */}
            {decisions.length > 0 && (
                <div className="row mt-4 g-4">
                    <div className="col-md-3">
                        <div className="neon-card text-center">
                            <div className="card-header-neon">Toplam Karar</div>
                            <div className="metric-big text-neon-blue" style={{ fontSize: '2.5rem' }}>
                                {decisions.length}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="neon-card text-center">
                            <div className="card-header-neon">Force 2FA</div>
                            <div className="metric-big text-neon-green" style={{ fontSize: '2.5rem' }}>
                                {decisions.filter(d => d.selectedAction === 'FORCE_2FA').length}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="neon-card text-center">
                            <div className="card-header-neon">Payment Review</div>
                            <div className="metric-big text-neon-yellow" style={{ fontSize: '2.5rem' }}>
                                {decisions.filter(d => d.selectedAction === 'PAYMENT_REVIEW').length}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-3">
                        <div className="neon-card text-center">
                            <div className="card-header-neon">Fraud Case</div>
                            <div className="metric-big text-neon-red" style={{ fontSize: '2.5rem' }}>
                                {decisions.filter(d => d.selectedAction === 'OPEN_FRAUD_CASE').length}
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Decisions;
