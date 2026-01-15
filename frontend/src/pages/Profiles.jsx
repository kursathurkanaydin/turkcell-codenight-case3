import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, ProgressBar } from 'react-bootstrap';

function Profiles() {
    const [profiles, setProfiles] = useState([]);

    useEffect(() => {
        fetchProfiles();
    }, []);

    const fetchProfiles = async () => {
        try {
            const res = await axios.get('http://localhost:8080/risk-profiles');
            setProfiles(res.data);
        } catch (e) {
            console.error(e);
        }
    };

    const getVariant = (score) => {
        if (score < 30) return 'success';
        if (score < 70) return 'warning';
        return 'danger';
    };

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2>RİSK PROFİLLERİ</h2>
                    <small className="text-muted">USER RISK SCORING & SIGNALS</small>
                </div>
            </div>

            <div className="neon-card">
                <Table className="table-neon" responsive>
                    <thead>
                        <tr>
                            <th>KULLANICI</th>
                            <th>RİSK SKORU</th>
                            <th>RİSK SEVİYESİ</th>
                            <th>SİNYALLER</th>
                        </tr>
                    </thead>
                    <tbody>
                        {(!profiles || profiles.length === 0) ? (
                            <tr><td colSpan="4" className="text-center text-muted py-4">Veri bulunamadı veya yükleniyor...</td></tr>
                        ) : profiles.map(p => (
                            <tr key={p.userId}>
                                <td>
                                    <div className="fw-bold text-white mb-1" style={{ fontSize: '1.1rem' }}>{p.userId}</div>
                                </td>
                                <td style={{ width: '200px' }}>
                                    <div className="d-flex align-items-center">
                                        <div className="flex-grow-1 me-3">
                                            <ProgressBar
                                                now={p.riskScore}
                                                max={100}
                                                variant={p.riskScore > 80 ? 'danger' : p.riskScore > 50 ? 'warning' : 'success'}
                                                style={{ height: '6px', backgroundColor: 'rgba(255,255,255,0.1)' }}
                                            />
                                        </div>
                                        <span className={`fw-bold ${p.riskScore > 80 ? 'text-neon-red' : p.riskScore > 50 ? 'text-neon-yellow' : 'text-neon-green'}`}>
                                            {p.riskScore}
                                        </span>
                                    </div>
                                </td>
                                <td>
                                    <span className={`badge-pro ${p.riskLevel === 'HIGH' ? 'danger' : p.riskLevel === 'MEDIUM' ? 'warning' : 'success'}`}>
                                        {p.riskLevel}
                                    </span>
                                </td>
                                <td>
                                    <span className="font-monospace" style={{ color: '#e2e8f0', fontSize: '0.85rem' }}>
                                        {(p.signals && p.signals.length > 0)
                                            ? p.signals.join(' | ')
                                            : <span style={{ opacity: 0.5 }}>NO SIGNALS</span>}
                                    </span>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>
        </div>
    );
}

export default Profiles;
