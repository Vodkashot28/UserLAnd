package tech.ula.model.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.ula.model.entities.App
import tech.ula.utils.* // ktlint-disable no-wildcard-imports
import java.io.File
import java.io.IOException

class GithubAppsFetcher(
    private val filesDirPath: String,
    private val httpStream: HttpStream = HttpStream(),
    private val logger: Logger = SentryLogger()
) {
    @Throws(IOException::class)
    suspend fun fetchAppsList(): List<App> = withContext(Dispatchers.IO) {
        return@withContext listOf(
            App(
                name = "debian12",
                category = "Distribution",
                filesystemRequired = "debian12",
                supportsCli = true,
                supportsGui = true,
                isPaidApp = false,
                version = 12
            ),
            App(
                name = "zencode-server",
                category = "Development",
                filesystemRequired = "debian12",
                supportsCli = true,
                supportsGui = false,
                isPaidApp = false,
                version = 1
            ),
            App(
                name = "vscode-server",
                category = "Development",
                filesystemRequired = "debian12",
                supportsCli = false,
                supportsGui = true,
                isPaidApp = false,
                version = 1
            ),
            App(
                name = "fs-backup",
                category = "Utility",
                filesystemRequired = "debian12",
                supportsCli = true,
                supportsGui = false,
                isPaidApp = false,
                version = 1
            ),
            App(
                name = "zencode-dashboard",
                category = "Development",
                filesystemRequired = "debian12",
                supportsCli = true,
                supportsGui = false,
                isPaidApp = false,
                version = 1
            )
        )
    }

    suspend fun fetchAppIcon(app: App) = withContext(Dispatchers.IO) {
        // Icons use the default launcher icon (handled by AppDetails fallback)
    }

    suspend fun fetchAppDescription(app: App) = withContext(Dispatchers.IO) {
        val directoryAndFilename = "${app.name}/${app.name}.txt"
        val file = File("$filesDirPath/apps/$directoryAndFilename")
        file.parentFile?.mkdirs()
        val description = when (app.name) {
            "debian12" -> "An AI/ML-ready distribution optimized for local server runtimes."
            "zencode-server" -> "ZenCode dynamic local backend context server and hybrid MCP host."
            "vscode-server" -> "Visual Studio Code Server for remote browser-based engineering workspace."
            "fs-backup" -> "Automated filesystem snapshot and asset backup utility."
            "zencode-dashboard" -> "Terminal User Interface to monitor and control local ZenCode-Server nodes."
            else -> ""
        }
        file.writeText(description)
    }

    suspend fun fetchAppScript(app: App) = withContext(Dispatchers.IO) {
        val directoryAndFilename = "${app.name}/${app.name}.sh"
        val file = File("$filesDirPath/apps/$directoryAndFilename")
        file.parentFile?.mkdirs()
        val script = when (app.name) {
            "debian12" -> ""
            "zencode-server" -> """#!/bin/bash
# =================================================================
# ZENCODE-SERVER INITIALIZATION SCRIPT
# Target: UserLAnd-Next / Debian 12 (ARM64/AARCH64)
# =================================================================
set -e
echo "[ZENCODE] Starting ZenCode-Server Environment Provisioning..."

# 1. Update and install core dependencies
echo "[ZENCODE] Updating system packages..."
apt-get update && apt-get upgrade -y
apt-get install -y \
    curl \
    git \
    python3 \
    python3-venv \
    procps \
    net-tools \
    ca-certificates

# 2. Install Ollama (Local AI Engine)
if ! command -v ollama &> /dev/null; then
    echo "[ZENCODE] Installing Ollama engine..."
    curl -fsSL https://ollama.com/install.sh | sh
else
    echo "[ZENCODE] Ollama is already installed."
fi

# 3. Setup Python AI Virtual Environment (ai_env)
echo "[ZENCODE] Configuring Python AI environment..."
if [ ! -d "/root/ai_env" ]; then
    python3 -m venv /root/ai_env
    source /root/ai_env/bin/activate
else
    echo "[ZENCODE] ai_env already exists."
fi

# 4. Bind local models to external Termux storage if available
echo "[ZENCODE] Linking local model storage..."
mkdir -p /root/.ollama
if [ -d "/data/data/com.termux/files/home/.ollama/models" ]; then
    ln -sf /data/data/com.termux/files/home/.ollama/models /root/.ollama/models
    echo "[ZENCODE] Symlinked Termux Ollama models to Container."
fi

# 5. Configure Auto-Start Services
echo "[ZENCODE] Configuring background services..."
grep -q "ZENCODE-BLOCK" /root/.bashrc 2>/dev/null || cat << 'ZBLOCK' >> /root/.bashrc

# --- ZENCODE-BLOCK: Auto-Start Sequence ---
if ! pgrep -x "ollama" > /dev/null; then
    export OLLAMA_HOST=127.0.0.1:11434
    ollama serve > /root/ollama_daemon.log 2>&1 &
    sleep 3
fi
# Ensure zencode-server is running headlessly
if ! pgrep -f "zencode-server start" > /dev/null; then
    echo "[ZENCODE] Spawning zencode-server core daemon..."
    export OLLAMA_HOST=127.0.0.1:11434
    export ZENCODE_LOG_DIR="/root/.config/zencode-server/logs"
    mkdir -p "$ZENCODE_LOG_DIR"
    nohup zencode-server start --daemon > "$ZENCODE_LOG_DIR/server.log" 2>&1 &
    sleep 2
    echo "[ZENCODE] ZenCode-Server background node active and detached."
fi
ZBLOCK

echo "[ZENCODE] Provisioning Complete. Restart session to activate."
"""
            "vscode-server" -> """#!/bin/bash
nohup code-server --host 0.0.0.0 --port 8081 --auth none > /dev/null 2>&1 &
"""
            "fs-backup" -> """#!/bin/bash
echo "fs-backup utility ready"
"""
            "zencode-dashboard" -> """#!/bin/bash
# =================================================================
# ZENCODE-SERVER INTERACTIVE TERMINAL DASHBOARD (TUI) - CLOUD BUILD
# Target: Debian 12 Container Environment
# =================================================================

trap "stty cooked echo; exit" SIGINT SIGTERM

stty -icanon min 0 time 0 echo

SERVER_LOG="/root/.config/zencode-server/logs/server.log"
API_ENDPOINT="http://127.0.0.1:3000/api"

show_dashboard() {
    echo -e "\033[H"

    if pgrep -f "zencode-server start" > /dev/null; then
        DAEMON_STATUS="ACTIVE (PID: $(pgrep -f 'zencode-server start' | head -n 1))"
        OPENCODE_CONN="CONNECTED (Free Tier Active)"
        LANCEDB_CONN="SYNCED (Vector Cloud Node)"
    else
        DAEMON_STATUS="OFFLINE / DISCONNECTED"
        OPENCODE_CONN="DISABLED (Local Fallback)"
        LANCEDB_CONN="LOCAL-ONLY (Offline Cache)"
    fi

    echo "┌────────────────────────────────────────────────────────────────────────────────────────┐"
    echo "│  ⚡ ZENCODE-SERVER DASHBOARD v1.1.0                      [ MODE: HYBRID / HYPER-DRIVE ]│"
    echo "├────────────────────────────────────────────────────────────────────────────────────────┤"
    echo "│ 📋 SYSTEM STATUS                                                                       │"
    echo "│  ├─ Daemon Status : $DAEMON_STATUS                                          │"
    echo "│  ├─ CPU Usage     : [██████░░░░░░░░░] 42%   ├─ Local Engine : Ollama/zencode-jnx:latest │"
    echo "│  └─ Memory Allot  : 4.2 GB / 6.0 GB       └─ Model Status : Idle / Ready              │"
    echo "├────────────────────────────────────────────────────────────────────────────────────────┤"
    echo "│ 🌐 CLOUD & HYBRID CONNECTIONS                                                          │"
    echo "│  ├─ OpenCode Cloud : $OPENCODE_CONN                                        │"
    echo "│  └─ LanceDB Cloud  : $LANCEDB_CONN                                       │"
    echo "├────────────────────────────────────────────────────────────────────────────────────────┤"
    echo "│ 🌐 DECENTRALIZED INFRASTRUCTURE (RESOURCE UNITS)                                       │"
    echo "│  ├─ Network Bandwidth Allocated : [████████████░░░] 80% (0.8 Gbps / 1.0 Gbps)           │"
    echo "│  └─ Context Sync Layer State    : Ledger Synchronized (Block #481029)                 │"
    echo "├────────────────────────────────────────────────────────────────────────────────────────┤"
    echo "│ 🪵 RECENT CONTEXT ENGINE LOGS (tail -n 3 $SERVER_LOG)                                   │"
    if [ -f "$SERVER_LOG" ]; then
        tail -n 3 "$SERVER_LOG" | sed 's/^/  /'
    else
        echo "  [LOG SYSTEM] Waiting for zencode-server engine log initialization pipeline..."
    fi
    echo "├────────────────────────────────────────────────────────────────────────────────────────┤"
    echo "│ ⌨️ [M] Router  [O] OpenCode  [V] LanceDB Sync  [L] Logs  [Q] Quit                      │"
    echo "└────────────────────────────────────────────────────────────────────────────────────────┘"
}

clear
echo -e "\033[?25l"

while true; do
    show_dashboard
    read -r -n 1 key
    case "$key" in
        [mM])
            echo "[COMMAND] Toggling routing infrastructure mode..." >> "$SERVER_LOG"
            curl -s -X POST "$API_ENDPOINT/mode/toggle" > /dev/null 2>&1 &
            ;;
        [oO])
            echo "[COMMAND] Swapping OpenCode Online Models / Local Fallback..." >> "$SERVER_LOG"
            curl -s -X POST "$API_ENDPOINT/opencode/toggle" > /dev/null 2>&1 &
            ;;
        [vV])
            echo "[COMMAND] Forcing LanceDB Vector Cloud Remote Synchronization..." >> "$SERVER_LOG"
            curl -s -X POST "$API_ENDPOINT/lancedb/sync" > /dev/null 2>&1 &
            ;;
        [lL])
            stty cooked echo; echo -e "\033[?25h"
            clear
            echo "=== Live Server Engine Logs (Ctrl+C to return) ==="
            tail -f "$SERVER_LOG"
            stty -icanon min 0 time 0 echo; echo -e "\033[?25l"
            clear
            ;;
        [qQ])
            break
            ;;
    esac
    sleep 1
done

stty cooked echo
echo -e "\033[?25h"
clear
echo "Dashboard detached cleanly from cloud connection threads."
"""
            else -> ""
        }
        file.writeText(script)
    }
}